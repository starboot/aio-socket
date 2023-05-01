package cn.starboot.mqtt.codec;

import cn.starboot.socket.codec.EncoderException;
import cn.starboot.socket.codec.util.ByteBufferUtil;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioEncoderException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static cn.starboot.mqtt.codec.MqttConstant.DEFAULT_MAX_CLIENT_ID_LENGTH;

/**
 * Encodes Mqtt messages into bytes following the protocol specification v3.1
 * as described here <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html">MQTTV3.1</a>
 * or v5.0 as described here <a href="https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html">MQTTv5.0</a> -
 * depending on the version specified in the first CONNECT message that goes through the channel.
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttEncoder {

	private static MqttEncoder mqttEncoder = null;

	private MqttEncoder() {
	}

	public synchronized static MqttEncoder getInstance() {
		if (Objects.isNull(mqttEncoder)) {
			mqttEncoder = new MqttEncoder();
		}
		return mqttEncoder;
	}

	/**
	 * This is the main encoding method.
	 * It's only visible for testing.
	 *
	 * @param ctx     ChannelContext
	 * @param message MQTT message to encode
	 */
	public void doEncode(ChannelContext ctx, MqttMessage message) throws AioEncoderException {
		WriteBuffer writeBuffer = ctx.getWriteBuffer();
		switch (message.fixedHeader().messageType()) {
			case CONNECT:
				encodeConnectMessage(ctx, (MqttConnectMessage) message, writeBuffer);
				break;
			case CONNACK:
				encodeConnAckMessage(ctx, (MqttConnAckMessage) message, writeBuffer);
				break;
			case PUBLISH:
				encodePublishMessage(ctx, (MqttPublishMessage) message, writeBuffer);
				break;
			case SUBSCRIBE:
				encodeSubscribeMessage(ctx, (MqttSubscribeMessage) message, writeBuffer);
				break;
			case UNSUBSCRIBE:
				encodeUnsubscribeMessage(ctx, (MqttUnsubscribeMessage) message, writeBuffer);
				break;
			case SUBACK:
				encodeSubAckMessage(ctx, (MqttSubAckMessage) message, writeBuffer);
				break;
			case UNSUBACK:
				if (message instanceof MqttUnsubAckMessage) {
					encodeUnsubAckMessage(ctx, (MqttUnsubAckMessage) message, writeBuffer);
				}
				encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(message, writeBuffer);
				break;
			case PUBACK:
			case PUBREC:
			case PUBREL:
			case PUBCOMP:
				encodePubReplyMessage(ctx, message, writeBuffer);
				break;
			case DISCONNECT:
			case AUTH:
				encodeReasonCodePlusPropertiesMessage(ctx, message, writeBuffer);
				break;
			case PINGREQ:
			case PINGRESP:
				encodeMessageWithOnlySingleByteFixedHeader(message, writeBuffer);
				break;
			default:
				throw new IllegalArgumentException("Unknown message type: " + message.fixedHeader().messageType().value());
		}
	}

	private static void encodeConnectMessage(ChannelContext ctx,
											 MqttConnectMessage message,
											 WriteBuffer writeBuffer) throws AioEncoderException {
		int payloadBufferSize = 0;

		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttConnectVariableHeader variableHeader = message.variableHeader();
		MqttConnectPayload payload = message.payload();
		MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte) variableHeader.version());
		MqttCodecUtil.setMqttVersion(ctx, mqttVersion);

		// as MQTT 3.1 & 3.1.1 spec, If the User Name Flag is set to 0, the Password Flag MUST be set to 0
		if (!variableHeader.hasUserName() && variableHeader.hasPassword()) {
			throw new EncoderException("Without a username, the password MUST be not set");
		}

		// Client id
		String clientIdentifier = payload.clientIdentifier();
		if (!MqttCodecUtil.isValidClientId(mqttVersion, DEFAULT_MAX_CLIENT_ID_LENGTH, clientIdentifier)) {
			throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + clientIdentifier);
		}
		byte[] clientIdentifierBytes = encodeStringUtf8(clientIdentifier);
		payloadBufferSize += 2 + clientIdentifierBytes.length;

		// Will topic and message
		String willTopic = payload.willTopic();
		byte[] willTopicBytes = willTopic != null ? encodeStringUtf8(willTopic) : ByteBufferUtil.EMPTY_BYTES;
		byte[] willMessage = payload.willMessageInBytes();
		byte[] willMessageBytes = willMessage != null ? willMessage : ByteBufferUtil.EMPTY_BYTES;
		if (variableHeader.isWillFlag()) {
			payloadBufferSize += 2 + willTopicBytes.length;
			payloadBufferSize += 2 + willMessageBytes.length;
		}

		String userName = payload.userName();
		byte[] userNameBytes = userName != null ? encodeStringUtf8(userName) : ByteBufferUtil.EMPTY_BYTES;
		if (variableHeader.hasUserName()) {
			payloadBufferSize += 2 + userNameBytes.length;
		}

		byte[] password = payload.passwordInBytes();
		byte[] passwordBytes = password != null ? password : ByteBufferUtil.EMPTY_BYTES;
		if (variableHeader.hasPassword()) {
			payloadBufferSize += 2 + passwordBytes.length;
		}

		// Fixed and variable header
		byte[] protocolNameBytes = mqttVersion.protocolNameBytes();
		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion, message.variableHeader().properties());

		final byte[] willPropertiesBytes;
		if (variableHeader.isWillFlag()) {
			willPropertiesBytes = encodePropertiesIfNeeded(mqttVersion, payload.willProperties());
			payloadBufferSize += propertiesBytes.length;
		} else {
			willPropertiesBytes = ByteBufferUtil.EMPTY_BYTES;
		}
		int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4 + propertiesBytes.length;

		int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
		// 申请 ByteBuffer
//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeVariableLengthInt(writeBuffer, variablePartSize);
		writeBuffer.writeShort((short) protocolNameBytes.length);
		writeBuffer.write(protocolNameBytes);

		writeBuffer.write((byte) variableHeader.version());
		writeBuffer.write((byte) getConnVariableHeaderFlag(variableHeader));
		writeBuffer.writeShort((short) variableHeader.keepAliveTimeSeconds());
		writeBuffer.write(propertiesBytes);

		// Payload
		writeBuffer.writeShort((short) clientIdentifierBytes.length);
		writeBuffer.write(clientIdentifierBytes, 0, clientIdentifierBytes.length);
		if (variableHeader.isWillFlag()) {
			writeBuffer.write(willPropertiesBytes, 0, willPropertiesBytes.length);
			writeBuffer.writeShort((short) willTopicBytes.length);
			writeBuffer.write(willTopicBytes, 0, willTopicBytes.length);
			writeBuffer.writeShort((short) willMessageBytes.length);
			writeBuffer.write(willMessageBytes, 0, willMessageBytes.length);
		}
		if (variableHeader.hasUserName()) {
			writeBuffer.writeShort((short) userNameBytes.length);
			writeBuffer.write(userNameBytes, 0, userNameBytes.length);
		}
		if (variableHeader.hasPassword()) {
			writeBuffer.writeShort((short) passwordBytes.length);
			writeBuffer.write(passwordBytes, 0, passwordBytes.length);
		}
	}

	private static int getConnVariableHeaderFlag(MqttConnectVariableHeader variableHeader) {
		int flagByte = 0;
		if (variableHeader.hasUserName()) {
			flagByte |= 0x80;
		}
		if (variableHeader.hasPassword()) {
			flagByte |= 0x40;
		}
		if (variableHeader.isWillRetain()) {
			flagByte |= 0x20;
		}
		flagByte |= (variableHeader.willQos() & 0x03) << 3;
		if (variableHeader.isWillFlag()) {
			flagByte |= 0x04;
		}
		if (variableHeader.isCleanSession()) {
			flagByte |= 0x02;
		}
		return flagByte;
	}

	private static void encodeConnAckMessage(ChannelContext ctx,
											 MqttConnAckMessage message,
											 WriteBuffer writeBuffer) throws AioEncoderException {
		final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion, message.variableHeader().properties());
//		ByteBuffer buf = allocator.allocate(4 + propertiesBytes.length);
		writeBuffer.write((byte) getFixedHeaderByte1(message.fixedHeader()));
		writeVariableLengthInt(writeBuffer, 2 + propertiesBytes.length);
		writeBuffer.write((byte) (message.variableHeader().isSessionPresent() ? 0x01 : 0x00));
		writeBuffer.write(message.variableHeader().connectReturnCode().value());
		writeBuffer.write(propertiesBytes);
	}

	private static void encodeSubscribeMessage(ChannelContext ctx,
											   MqttSubscribeMessage message,
											   WriteBuffer writeBuffer) throws AioEncoderException {
		MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion,
				message.idAndPropertiesVariableHeader().properties());

		final int variableHeaderBufferSize = 2 + propertiesBytes.length;
		int payloadBufferSize = 0;

		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttMessageIdVariableHeader variableHeader = message.variableHeader();
		MqttSubscribePayload payload = message.payload();

		for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
			String topicName = topic.topicName();
			byte[] topicNameBytes = encodeStringUtf8(topicName);
			payloadBufferSize += 2 + topicNameBytes.length;
			payloadBufferSize += 1;
		}

		int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeVariableLengthInt(writeBuffer, variablePartSize);

		// Variable Header
		int messageId = variableHeader.messageId();
		writeBuffer.writeShort((short) messageId);
		writeBuffer.write(propertiesBytes);

		// Payload
		for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
			// topicName
			String topicName = topic.topicName();
			byte[] topicNameBytes = encodeStringUtf8(topicName);
			writeBuffer.writeShort((short) topicNameBytes.length);
			writeBuffer.write(topicNameBytes, 0, topicNameBytes.length);
			if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_3_1) {
				writeBuffer.write((byte) topic.qualityOfService().value());
			} else {
				// option
				final MqttSubscriptionOption option = topic.option();
				int optionEncoded = option.retainHandling().value() << 4;
				if (option.isRetainAsPublished()) {
					optionEncoded |= 0x08;
				}
				if (option.isNoLocal()) {
					optionEncoded |= 0x04;
				}
				optionEncoded |= option.qos().value();
				writeBuffer.write((byte) optionEncoded);
			}
		}
	}

	private static void encodeUnsubscribeMessage(ChannelContext ctx,
												 MqttUnsubscribeMessage message,
												 WriteBuffer writeBuffer) throws AioEncoderException {
		MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion,
				message.idAndPropertiesVariableHeader().properties());

		final int variableHeaderBufferSize = 2 + propertiesBytes.length;
		int payloadBufferSize = 0;

		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttMessageIdVariableHeader variableHeader = message.variableHeader();
		MqttUnsubscribePayload payload = message.payload();

		for (String topicName : payload.topics()) {
			byte[] topicNameBytes = encodeStringUtf8(topicName);
			payloadBufferSize += 2 + topicNameBytes.length;
		}

		int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeVariableLengthInt(writeBuffer, variablePartSize);

		// Variable Header
		int messageId = variableHeader.messageId();
		writeBuffer.writeShort((short) messageId);
		writeBuffer.write(propertiesBytes);

		// Payload
		for (String topicName : payload.topics()) {
			// topicName
			byte[] topicNameBytes = encodeStringUtf8(topicName);
			writeBuffer.writeShort((short) topicNameBytes.length);
			writeBuffer.write(topicNameBytes, 0, topicNameBytes.length);
		}
	}

	private static void encodeSubAckMessage(ChannelContext ctx,
											MqttSubAckMessage message,
											WriteBuffer writeBuffer) throws AioEncoderException {
		MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion,
				message.idAndPropertiesVariableHeader().properties());
		int variableHeaderBufferSize = 2 + propertiesBytes.length;
		int payloadBufferSize = message.payload().grantedQoSLevels().size();
		int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
		writeBuffer.write((byte) getFixedHeaderByte1(message.fixedHeader()));
		writeVariableLengthInt(writeBuffer, variablePartSize);
		writeBuffer.writeShort((short) message.variableHeader().messageId());
		writeBuffer.write(propertiesBytes);
		for (int code : message.payload().reasonCodes()) {
			writeBuffer.write((byte) code);
		}
	}

	private static void encodeUnsubAckMessage(ChannelContext ctx,
											  MqttUnsubAckMessage message,
											  WriteBuffer writeBuffer) throws AioEncoderException {
		if (message.variableHeader() instanceof MqttMessageIdAndPropertiesVariableHeader) {
			MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
			byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion, message.idAndPropertiesVariableHeader().properties());

			int variableHeaderBufferSize = 2 + propertiesBytes.length;
			MqttUnsubAckPayload payload = message.payload();
			int payloadBufferSize = payload == null ? 0 : payload.unsubscribeReasonCodes().size();
			int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
			int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);
//			ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
			writeBuffer.write((byte) getFixedHeaderByte1(message.fixedHeader()));
			writeVariableLengthInt(writeBuffer, variablePartSize);
			writeBuffer.writeShort((short) message.variableHeader().messageId());
			writeBuffer.write(propertiesBytes);

			if (payload != null) {
				for (Short reasonCode : payload.unsubscribeReasonCodes()) {
					writeBuffer.writeShort(reasonCode);
				}
			}
		} else {
			encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(message, writeBuffer);
		}
	}

	private static void encodePublishMessage(ChannelContext ctx,
											 MqttPublishMessage message,
											 WriteBuffer writeBuffer) throws AioEncoderException {
		MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttPublishVariableHeader variableHeader = message.variableHeader();
		ByteBuffer payload = message.payload().duplicate();

		String topicName = variableHeader.topicName();
		byte[] topicNameBytes = encodeStringUtf8(topicName);

		byte[] propertiesBytes = encodePropertiesIfNeeded(mqttVersion,
				message.variableHeader().properties());

		int variableHeaderBufferSize = 2 + topicNameBytes.length +
				(mqttFixedHeader.qosLevel().value() > 0 ? 2 : 0) + propertiesBytes.length;
		int payloadBufferSize = payload.array().length;
		int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize);

//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variablePartSize);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeVariableLengthInt(writeBuffer, variablePartSize);
		writeBuffer.writeShort((short) topicNameBytes.length);
		writeBuffer.write(topicNameBytes);
		if (mqttFixedHeader.qosLevel().value() > 0) {
			writeBuffer.writeShort((short) variableHeader.packetId());
		}
		writeBuffer.write(propertiesBytes);
		writeBuffer.write(payload.array());
	}

	private static void encodePubReplyMessage(ChannelContext ctx,
											  MqttMessage message,
											  WriteBuffer writeBuffer) throws AioEncoderException {
		if (message.variableHeader() instanceof MqttPubReplyMessageVariableHeader) {
			MqttFixedHeader mqttFixedHeader = message.fixedHeader();
			MqttPubReplyMessageVariableHeader variableHeader =
					(MqttPubReplyMessageVariableHeader) message.variableHeader();
			final byte[] propertiesBytes;
			final boolean includeReasonCode;
			final int variableHeaderBufferSize;
			final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
			if (mqttVersion == MqttVersion.MQTT_5 &&
					(variableHeader.reasonCode() != MqttPubReplyMessageVariableHeader.REASON_CODE_OK ||
							!variableHeader.properties().isEmpty())) {
				propertiesBytes = encodeProperties(variableHeader.properties());
				includeReasonCode = true;
				variableHeaderBufferSize = 3 + propertiesBytes.length;
			} else {
				propertiesBytes = ByteBufferUtil.EMPTY_BYTES;
				includeReasonCode = false;
				variableHeaderBufferSize = 2;
			}

			final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
//			ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variableHeaderBufferSize);
			writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
			writeVariableLengthInt(writeBuffer, variableHeaderBufferSize);
			writeBuffer.writeShort((short) variableHeader.messageId());
			if (includeReasonCode) {
				writeBuffer.write(variableHeader.reasonCode());
			}
			writeBuffer.write(propertiesBytes);
		} else {
			encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(message, writeBuffer);
		}
	}

	private static void encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(MqttMessage message,
																			   WriteBuffer writeBuffer) throws AioEncoderException {
		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
		// variable part only has a message id
		int variableHeaderBufferSize = 2;
		int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
//		ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variableHeaderBufferSize);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeVariableLengthInt(writeBuffer, variableHeaderBufferSize);
		writeBuffer.writeShort((short) variableHeader.messageId());
	}

	private static void encodeReasonCodePlusPropertiesMessage(ChannelContext ctx,
															  MqttMessage message,
															  WriteBuffer writeBuffer) throws AioEncoderException {
		if (message.variableHeader() instanceof MqttReasonCodeAndPropertiesVariableHeader) {
			MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
			MqttFixedHeader mqttFixedHeader = message.fixedHeader();
			MqttReasonCodeAndPropertiesVariableHeader variableHeader =
					(MqttReasonCodeAndPropertiesVariableHeader) message.variableHeader();

			final byte[] propertiesBytes;
			final boolean includeReasonCode;
			final int variableHeaderBufferSize;
			if (mqttVersion == MqttVersion.MQTT_5 &&
					(variableHeader.reasonCode() != MqttReasonCodeAndPropertiesVariableHeader.REASON_CODE_OK ||
							!variableHeader.properties().isEmpty())) {
				propertiesBytes = encodeProperties(variableHeader.properties());
				includeReasonCode = true;
				variableHeaderBufferSize = 1 + propertiesBytes.length;
			} else {
				propertiesBytes = ByteBufferUtil.EMPTY_BYTES;
				includeReasonCode = false;
				variableHeaderBufferSize = 0;
			}
			final int fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize);
//			ByteBuffer buf = allocator.allocate(fixedHeaderBufferSize + variableHeaderBufferSize);
			writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
			writeVariableLengthInt(writeBuffer, variableHeaderBufferSize);
			if (includeReasonCode) {
				writeBuffer.write(variableHeader.reasonCode());
			}
			writeBuffer.write(propertiesBytes);
		} else {
			encodeMessageWithOnlySingleByteFixedHeader(message, writeBuffer);
		}
	}

	private static void encodeMessageWithOnlySingleByteFixedHeader(MqttMessage message,
																   WriteBuffer writeBuffer) {
		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
//		ByteBuffer buf = allocator.allocate(2);
		writeBuffer.write((byte) getFixedHeaderByte1(mqttFixedHeader));
		writeBuffer.write((byte) 0);
	}

	private static byte[] encodePropertiesIfNeeded(MqttVersion mqttVersion,
												   MqttProperties mqttProperties) {
		if (mqttVersion == MqttVersion.MQTT_5) {
			return encodeProperties(mqttProperties);
		}
		return ByteBufferUtil.EMPTY_BYTES;
	}

	private static byte[] encodeProperties(MqttProperties mqttProperties) {
//		WriteBuffer writeBuffer = new WriteBuffer();
		MqttByteBuffer mqttByteBuffer = new MqttByteBuffer();
		for (MqttProperties.MqttProperty<?> property : mqttProperties.listAll()) {
			MqttProperties.MqttPropertyType propertyType = MqttProperties.MqttPropertyType.valueOf(property.propertyId);
			switch (propertyType) {
				case PAYLOAD_FORMAT_INDICATOR:
				case REQUEST_PROBLEM_INFORMATION:
				case REQUEST_RESPONSE_INFORMATION:
				case MAXIMUM_QOS:
				case RETAIN_AVAILABLE:
				case WILDCARD_SUBSCRIPTION_AVAILABLE:
				case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
				case SHARED_SUBSCRIPTION_AVAILABLE:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					final byte bytePropValue = ((MqttProperties.IntegerProperty) property).value.byteValue();
					mqttByteBuffer.writeByte(bytePropValue);
					break;
				case SERVER_KEEP_ALIVE:
				case RECEIVE_MAXIMUM:
				case TOPIC_ALIAS_MAXIMUM:
				case TOPIC_ALIAS:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					final short twoBytesInPropValue =
							((MqttProperties.IntegerProperty) property).value.shortValue();
					mqttByteBuffer.writeShort(twoBytesInPropValue);
					break;
				case PUBLICATION_EXPIRY_INTERVAL:
				case SESSION_EXPIRY_INTERVAL:
				case WILL_DELAY_INTERVAL:
				case MAXIMUM_PACKET_SIZE:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					final int fourBytesIntPropValue = ((MqttProperties.IntegerProperty) property).value;
					mqttByteBuffer.writeInt(fourBytesIntPropValue);
					break;
				case SUBSCRIPTION_IDENTIFIER:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					final int vbi = ((MqttProperties.IntegerProperty) property).value;
					mqttByteBuffer.writeVarLengthInt(vbi);
					break;
				case CONTENT_TYPE:
				case RESPONSE_TOPIC:
				case ASSIGNED_CLIENT_IDENTIFIER:
				case AUTHENTICATION_METHOD:
				case RESPONSE_INFORMATION:
				case SERVER_REFERENCE:
				case REASON_STRING:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					writeEagerUTF8String(mqttByteBuffer, ((MqttProperties.StringProperty) property).value);
					break;
				case USER_PROPERTY:
					final List<MqttProperties.StringPair> pairs =
							((MqttProperties.UserProperties) property).value;
					for (MqttProperties.StringPair pair : pairs) {
						mqttByteBuffer.writeVarLengthInt(property.propertyId);
						writeEagerUTF8String(mqttByteBuffer, pair.key);
						writeEagerUTF8String(mqttByteBuffer, pair.value);
					}
					break;
				case CORRELATION_DATA:
				case AUTHENTICATION_DATA:
					mqttByteBuffer.writeVarLengthInt(property.propertyId);
					final byte[] binaryPropValue = ((MqttProperties.BinaryProperty) property).value;
					mqttByteBuffer.writeShort((short) binaryPropValue.length);
					mqttByteBuffer.writeBytes(binaryPropValue, 0, binaryPropValue.length);
					break;
				default:
					//shouldn't reach here
					throw new EncoderException("Unknown property type: " + propertyType);
			}
		}
		byte[] propertiesBytes = mqttByteBuffer.toArray();
		mqttByteBuffer.reset();
		mqttByteBuffer.writeVarLengthInt(propertiesBytes.length);
		mqttByteBuffer.writeBytes(propertiesBytes);
		return mqttByteBuffer.toArray();
	}

	private static int getFixedHeaderByte1(MqttFixedHeader header) {
		int ret = 0;
		ret |= header.messageType().value() << 4;
		if (header.isDup()) {
			ret |= 0x08;
		}
		ret |= header.qosLevel().value() << 1;
		if (header.isRetain()) {
			ret |= 0x01;
		}
		return ret;
	}

	private static void writeVariableLengthInt(WriteBuffer writeBuffer, int num) {
		do {
			int digit = num % 128;
			num /= 128;
			if (num > 0) {
				digit |= 0x80;
			}
			writeBuffer.write((byte) digit);
		} while (num > 0);
	}

	private static int getVariableLengthInt(int num) {
		int count = 0;
		do {
			num /= 128;
			count++;
		} while (num > 0);
		return count;
	}

	private static void writeEagerUTF8String(MqttByteBuffer mqttByteBuffer, String s) {
		if (s == null) {
			mqttByteBuffer.writeShort((short) 0);
		} else {
			byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
			mqttByteBuffer.writeShort((short) bytes.length);
			mqttByteBuffer.writeBytes(bytes);
		}
	}

	private static byte[] encodeStringUtf8(String s) {
		return s.getBytes(StandardCharsets.UTF_8);
	}
}
