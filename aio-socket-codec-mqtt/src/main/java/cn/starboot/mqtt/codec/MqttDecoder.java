package cn.starboot.mqtt.codec;

import cn.starboot.socket.codec.DecoderException;
import cn.starboot.socket.codec.DecoderResult;
import cn.starboot.socket.codec.util.ByteBufferUtil;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cn.starboot.mqtt.codec.MqttConstant.*;

/**
 * Decodes Mqtt messages from bytes, following
 * the MQTT protocol specification
 * <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html">v3.1</a>
 * or
 * <a href="https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html">v5.0</a>, depending on the
 * version specified in the CONNECT message that first goes through the channel.
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttDecoder {

	private final int maxBytesInMessage;
	private final int maxClientIdLength;

	public MqttDecoder() {
		this(DEFAULT_MAX_BYTES_IN_MESSAGE);
	}

	public MqttDecoder(int maxBytesInMessage) {
		this(maxBytesInMessage, DEFAULT_MAX_CLIENT_ID_LENGTH);
	}

	public MqttDecoder(int maxBytesInMessage, int maxClientIdLength) {
		this.maxBytesInMessage = maxBytesInMessage;
		this.maxClientIdLength = maxClientIdLength;
	}

	public MqttMessage doDecode(ChannelContext ctx, MemoryUnit memoryUnit) throws AioDecoderException {
		// 1. 半包
		MqttMessage message = decode(ctx, memoryUnit.buffer(), memoryUnit.buffer().remaining());
		if (message == null) {
			return null;
		}
		// 2. 解码异常
		DecoderResult decoderResult = message.decoderResult();
		if (decoderResult.isFailure()) {
			throw new AioDecoderException(decoderResult.getCause());
		}
		return message;
	}

	private MqttMessage decode(ChannelContext ctx, ByteBuffer buffer, int readableLength) throws AioDecoderException {
		// 1. 首先判断缓存中协议头是否读完（MQTT协议头为2字节）
		if (readableLength < MQTT_PROTOCOL_LENGTH) {
			return null;
		}
		// 2. 解析 FixedHeader 2~5 个字节
		MqttFixedHeader mqttFixedHeader;
		try {
			mqttFixedHeader = decodeFixedHeader(ctx, buffer);
		} catch (Exception cause) {
			return MqttMessageFactory.newInvalidMessage(cause);
		}
		// 包长度不够解析
		if (mqttFixedHeader == null) {
			return null;
		}
		// 包长度计算
		int headLength = mqttFixedHeader.headLength();
		int bytesRemainingInVariablePart = mqttFixedHeader.remainingLength();
		int messageLength = headLength + bytesRemainingInVariablePart;
		if (messageLength > maxBytesInMessage) {
			throw new AioDecoderException("too large message: " + messageLength + " bytes but maxBytesInMessage is " + maxBytesInMessage);
		}
		// 3. 长度不够，直接返回 null
		if (readableLength < messageLength) {
			return null;
		}
		// 4. 解析头信息
		Object variableHeader = null;
		try {
			Result<?> decodedVariableHeader = decodeVariableHeader(ctx, buffer, mqttFixedHeader, bytesRemainingInVariablePart);
			variableHeader = decodedVariableHeader.value;
			bytesRemainingInVariablePart -= decodedVariableHeader.numberOfBytesConsumed;
		} catch (Exception cause) {
			return MqttMessageFactory.newInvalidMessage(mqttFixedHeader, variableHeader, cause);
		}
		// 5. 解析消息体
		final Result<?> decodedPayload;
		try {
			decodedPayload = decodePayload(buffer, maxClientIdLength, mqttFixedHeader.messageType(),
					bytesRemainingInVariablePart, variableHeader);
			bytesRemainingInVariablePart -= decodedPayload.numberOfBytesConsumed;
			if (bytesRemainingInVariablePart != 0) {
				throw new DecoderException("non-zero remaining payload bytes: " +
						bytesRemainingInVariablePart + " (" + mqttFixedHeader.messageType() + ')');
			}
			return MqttMessageFactory.newMessage(mqttFixedHeader, variableHeader, decodedPayload.value);
		} catch (Throwable cause) {
			return MqttMessageFactory.newInvalidMessage(mqttFixedHeader, variableHeader, cause);
		}
	}

	/**
	 * Decodes the fixed header. It's one byte for the flags and then variable bytes for the remaining length.
	 *
	 * @param buffer the buffer to decode from
	 * @return the fixed header
	 */
	private static MqttFixedHeader decodeFixedHeader(ChannelContext ctx, ByteBuffer buffer) {
		short b1 = ByteBufferUtil.readUnsignedByte(buffer);
		MqttMessageType messageType = MqttMessageType.valueOf(b1 >> 4);
		boolean dupFlag = (b1 & 0x08) == 0x08;
		int qosLevel = (b1 & 0x06) >> 1;
		boolean retain = (b1 & 0x01) != 0;
		int remainingLength = 0;
		int multiplier = 1;
		short digit;
		int loops = 0;
		do {
			if (!buffer.hasRemaining()) {
				return null;
			}
			digit = ByteBufferUtil.readUnsignedByte(buffer);
			remainingLength += (digit & 127) * multiplier;
			multiplier *= 128;
			loops++;
		} while ((digit & 128) != 0 && loops < 4);
		// MQTT protocol limits Remaining Length to 4 bytes
		if (loops == 4 && (digit & 128) != 0) {
			throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
		}
		int headLength = 1 + loops;
		MqttFixedHeader decodedFixedHeader = new MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, headLength, remainingLength);
		return MqttCodecUtil.validateFixedHeader(ctx, MqttCodecUtil.resetUnusedFields(decodedFixedHeader));
	}

	/**
	 * Decodes the variable header (if any)
	 *
	 * @param buffer          the buffer to decode from
	 * @param mqttFixedHeader MqttFixedHeader of the same message
	 * @return the variable header
	 */
	private Result<?> decodeVariableHeader(ChannelContext ctx, ByteBuffer buffer,
										   MqttFixedHeader mqttFixedHeader,
										   int bytesRemainingInVariablePart) {
		switch (mqttFixedHeader.messageType()) {
			case CONNECT:
				return decodeConnectionVariableHeader(ctx, buffer);
			case CONNACK:
				return decodeConnAckVariableHeader(ctx, buffer);
			case UNSUBSCRIBE:
			case SUBSCRIBE:
			case SUBACK:
			case UNSUBACK:
				return decodeMessageIdAndPropertiesVariableHeader(ctx, buffer, mqttFixedHeader);
			case PUBACK:
			case PUBREC:
			case PUBCOMP:
			case PUBREL:
				return decodePubReplyMessage(buffer, mqttFixedHeader, bytesRemainingInVariablePart);
			case PUBLISH:
				return decodePublishVariableHeader(ctx, buffer, mqttFixedHeader);
			case DISCONNECT:
			case AUTH:
				return decodeReasonCodeAndPropertiesVariableHeader(buffer, bytesRemainingInVariablePart);
			case PINGREQ:
			case PINGRESP:
				// Empty variable header
				return new Result<>(null, 0);
			default:
				//shouldn't reach here
				throw new DecoderException("Unknown message type: " + mqttFixedHeader.messageType());
		}
	}

	private static Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(
			ChannelContext ctx, ByteBuffer buffer) {
		final Result<String> protoString = decodeString(buffer);
		int numberOfBytesConsumed = protoString.numberOfBytesConsumed;

		final byte protocolLevel = buffer.get();
		numberOfBytesConsumed += 1;

		MqttVersion version = MqttVersion.fromProtocolNameAndLevel(protoString.value, protocolLevel);
		MqttCodecUtil.setMqttVersion(ctx, version);

		final int b1 = ByteBufferUtil.readUnsignedByte(buffer);
		numberOfBytesConsumed += 1;

		final int keepAlive = decodeMsbLsb(buffer);
		numberOfBytesConsumed += 2;

		final boolean hasUserName = (b1 & 0x80) == 0x80;
		final boolean hasPassword = (b1 & 0x40) == 0x40;
		final boolean willRetain = (b1 & 0x20) == 0x20;
		final int willQos = (b1 & 0x18) >> 3;
		final boolean willFlag = (b1 & 0x04) == 0x04;
		final boolean cleanSession = (b1 & 0x02) == 0x02;
		if (version == MqttVersion.MQTT_3_1_1 || version == MqttVersion.MQTT_5) {
			final boolean zeroReservedFlag = (b1 & 0x01) == 0x0;
			if (!zeroReservedFlag) {
				// MQTT v3.1.1: The Server MUST validate that the reserved flag in the CONNECT Control Packet is
				// set to zero and disconnect the Client if it is not zero.
				// See https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349230
				throw new DecoderException("non-zero reserved flag");
			}
		}

		final MqttProperties properties;
		if (version == MqttVersion.MQTT_5) {
			final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
			properties = propertiesResult.value;
			numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
		} else {
			properties = MqttProperties.NO_PROPERTIES;
		}

		final MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
				version.protocolName(),
				version.protocolLevel(),
				hasUserName,
				hasPassword,
				willRetain,
				willQos,
				willFlag,
				cleanSession,
				keepAlive,
				properties);
		return new Result<>(mqttConnectVariableHeader, numberOfBytesConsumed);
	}

	private static Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(
			ChannelContext ctx, ByteBuffer buffer) {
		final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		final boolean sessionPresent = (ByteBufferUtil.readUnsignedByte(buffer) & 0x01) == 0x01;
		byte returnCode = buffer.get();
		int numberOfBytesConsumed = 2;

		final MqttProperties properties;
		if (mqttVersion == MqttVersion.MQTT_5) {
			final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
			properties = propertiesResult.value;
			numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
		} else {
			properties = MqttProperties.NO_PROPERTIES;
		}

		final MqttConnAckVariableHeader mqttConnAckVariableHeader =
				new MqttConnAckVariableHeader(MqttConnectReasonCode.valueOf(returnCode), sessionPresent, properties);
		return new Result<>(mqttConnAckVariableHeader, numberOfBytesConsumed);
	}

	private static Result<MqttMessageIdAndPropertiesVariableHeader> decodeMessageIdAndPropertiesVariableHeader(
			ChannelContext ctx, ByteBuffer buffer, MqttFixedHeader mqttFixedHeader) {
		final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		final int packetId = decodeMessageId(buffer, mqttFixedHeader);

		final MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader;
		final int mqtt5Consumed;

		if (mqttVersion == MqttVersion.MQTT_5) {
			final Result<MqttProperties> properties = decodeProperties(buffer);
			mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId, properties.value);
			mqtt5Consumed = properties.numberOfBytesConsumed;
		} else {
			mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(packetId,
					MqttProperties.NO_PROPERTIES);
			mqtt5Consumed = 0;
		}
		return new Result<>(mqttVariableHeader, 2 + mqtt5Consumed);
	}

	private Result<MqttPubReplyMessageVariableHeader> decodePubReplyMessage(
			ByteBuffer buffer, MqttFixedHeader mqttFixedHeader, int bytesRemainingInVariablePart) {
		final int packetId = decodeMessageId(buffer, mqttFixedHeader);
		final MqttPubReplyMessageVariableHeader mqttPubAckVariableHeader;
		final int consumed;
		final int packetIdNumberOfBytesConsumed = 2;
		if (bytesRemainingInVariablePart > 3) {
			final byte reasonCode = buffer.get();
			final Result<MqttProperties> properties = decodeProperties(buffer);
			mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
					reasonCode,
					properties.value);
			consumed = packetIdNumberOfBytesConsumed + 1 + properties.numberOfBytesConsumed;
		} else if (bytesRemainingInVariablePart > 2) {
			final byte reasonCode = buffer.get();
			mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
					reasonCode,
					MqttProperties.NO_PROPERTIES);
			consumed = packetIdNumberOfBytesConsumed + 1;
		} else {
			mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(packetId,
					(byte) 0,
					MqttProperties.NO_PROPERTIES);
			consumed = packetIdNumberOfBytesConsumed;
		}

		return new Result<>(mqttPubAckVariableHeader, consumed);
	}

	private Result<MqttReasonCodeAndPropertiesVariableHeader> decodeReasonCodeAndPropertiesVariableHeader(
			ByteBuffer buffer, int bytesRemainingInVariablePart) {
		final byte reasonCode;
		final MqttProperties properties;
		final int consumed;
		if (bytesRemainingInVariablePart > 1) {
			reasonCode = buffer.get();
			final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
			properties = propertiesResult.value;
			consumed = 1 + propertiesResult.numberOfBytesConsumed;
		} else if (bytesRemainingInVariablePart > 0) {
			reasonCode = buffer.get();
			properties = MqttProperties.NO_PROPERTIES;
			consumed = 1;
		} else {
			reasonCode = 0;
			properties = MqttProperties.NO_PROPERTIES;
			consumed = 0;
		}
		final MqttReasonCodeAndPropertiesVariableHeader mqttReasonAndPropsVariableHeader =
				new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties);
		return new Result<>(mqttReasonAndPropsVariableHeader, consumed);
	}

	private Result<MqttPublishVariableHeader> decodePublishVariableHeader(
			ChannelContext ctx, ByteBuffer buffer,
			MqttFixedHeader mqttFixedHeader) {
		final MqttVersion mqttVersion = MqttCodecUtil.getMqttVersion(ctx);
		final Result<String> decodedTopic = decodeString(buffer);
		if (!MqttCodecUtil.isValidPublishTopicName(decodedTopic.value)) {
			throw new DecoderException("invalid publish topic name: " + decodedTopic.value + " (contains wildcards)");
		}
		int numberOfBytesConsumed = decodedTopic.numberOfBytesConsumed;
		int messageId = -1;
		if (mqttFixedHeader.qosLevel().value() > 0) {
			messageId = decodeMessageId(buffer, mqttFixedHeader);
			numberOfBytesConsumed += 2;
		}
		final MqttProperties properties;
		if (mqttVersion == MqttVersion.MQTT_5) {
			final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
			properties = propertiesResult.value;
			numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
		} else {
			properties = MqttProperties.NO_PROPERTIES;
		}

		final MqttPublishVariableHeader mqttPublishVariableHeader =
				new MqttPublishVariableHeader(decodedTopic.value, messageId, properties);
		return new Result<>(mqttPublishVariableHeader, numberOfBytesConsumed);
	}

	/**
	 * @return messageId with numberOfBytesConsumed is 2
	 */
	private static int decodeMessageId(ByteBuffer buffer, MqttFixedHeader mqttFixedHeader) {
		final int messageId = decodeMsbLsb(buffer);
		// 注意：此处做 qos 降级处理，mqtt 规定 qos > 0，messageId 必须大于 0，固做降级处理
		if (messageId == 0) {
			mqttFixedHeader.downgradeQos();
		}
		return messageId;
	}

	/**
	 * Decodes the payload.
	 *
	 * @param buffer                       the buffer to decode from
	 * @param messageType                  type of the message being decoded
	 * @param bytesRemainingInVariablePart bytes remaining
	 * @param variableHeader               variable header of the same message
	 * @return the payload
	 */
	private static Result<?> decodePayload(ByteBuffer buffer, int maxClientIdLength,
										   MqttMessageType messageType, int bytesRemainingInVariablePart,
										   Object variableHeader) {
		switch (messageType) {
			case CONNECT:
				return decodeConnectionPayload(buffer, maxClientIdLength, (MqttConnectVariableHeader) variableHeader);
			case SUBSCRIBE:
				return decodeSubscribePayload(buffer, bytesRemainingInVariablePart);
			case SUBACK:
				return decodeSubAckPayload(buffer, bytesRemainingInVariablePart);
			case UNSUBSCRIBE:
				return decodeUnsubscribePayload(buffer, bytesRemainingInVariablePart);
			case UNSUBACK:
				return decodeUnsubAckPayload(buffer, bytesRemainingInVariablePart);
			case PUBLISH:
				return decodePublishPayload(buffer, bytesRemainingInVariablePart);
			default:
				// unknown payload , no byte consumed
				return new Result<>(null, 0);
		}
	}

	private static Result<MqttConnectPayload> decodeConnectionPayload(ByteBuffer buffer, int maxClientIdLength,
																	  MqttConnectVariableHeader mqttConnectVariableHeader) {
		final Result<String> decodedClientId = decodeString(buffer);
		final String decodedClientIdValue = decodedClientId.value;
		final MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectVariableHeader.name(),
				(byte) mqttConnectVariableHeader.version());
		if (!MqttCodecUtil.isValidClientId(mqttVersion, maxClientIdLength, decodedClientIdValue)) {
			throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + decodedClientIdValue);
		}
		int numberOfBytesConsumed = decodedClientId.numberOfBytesConsumed;

		Result<String> decodedWillTopic = null;
		byte[] decodedWillMessage = null;

		final MqttProperties willProperties;
		if (mqttConnectVariableHeader.isWillFlag()) {
			if (mqttVersion == MqttVersion.MQTT_5) {
				final Result<MqttProperties> propertiesResult = decodeProperties(buffer);
				willProperties = propertiesResult.value;
				numberOfBytesConsumed += propertiesResult.numberOfBytesConsumed;
			} else {
				willProperties = MqttProperties.NO_PROPERTIES;
			}
			decodedWillTopic = decodeString(buffer, 0, 32767);
			numberOfBytesConsumed += decodedWillTopic.numberOfBytesConsumed;
			decodedWillMessage = decodeByteArray(buffer);
			numberOfBytesConsumed += decodedWillMessage.length + 2;
		} else {
			willProperties = MqttProperties.NO_PROPERTIES;
		}
		Result<String> decodedUserName = null;
		byte[] decodedPassword = null;
		if (mqttConnectVariableHeader.hasUserName()) {
			decodedUserName = decodeString(buffer);
			numberOfBytesConsumed += decodedUserName.numberOfBytesConsumed;
		}
		if (mqttConnectVariableHeader.hasPassword()) {
			decodedPassword = decodeByteArray(buffer);
			numberOfBytesConsumed += decodedPassword.length + 2;
		}

		final MqttConnectPayload mqttConnectPayload =
				new MqttConnectPayload(
						decodedClientId.value,
						willProperties,
						decodedWillTopic != null ? decodedWillTopic.value : null,
						decodedWillMessage,
						decodedUserName != null ? decodedUserName.value : null,
						decodedPassword);
		return new Result<>(mqttConnectPayload, numberOfBytesConsumed);
	}

	private static Result<MqttSubscribePayload> decodeSubscribePayload(ByteBuffer buffer, int bytesRemainingInVariablePart) {
		final List<MqttTopicSubscription> subscribeTopics = new ArrayList<>();
		int numberOfBytesConsumed = 0;
		while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
			final Result<String> decodedTopicName = decodeString(buffer);
			numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
			//See 3.8.3.1 Subscription Options of MQTT 5.0 specification for optionByte details
			final short optionByte = ByteBufferUtil.readUnsignedByte(buffer);

			MqttQoS qos = MqttQoS.valueOf(optionByte & 0x03);
			boolean noLocal = ((optionByte & 0x04) >> 2) == 1;
			boolean retainAsPublished = ((optionByte & 0x08) >> 3) == 1;
			MqttSubscriptionOption.RetainedHandlingPolicy retainHandling =
					MqttSubscriptionOption.RetainedHandlingPolicy.valueOf((optionByte & 0x30) >> 4);

			final MqttSubscriptionOption subscriptionOption = new MqttSubscriptionOption(qos,
					noLocal, retainAsPublished, retainHandling);

			numberOfBytesConsumed++;
			subscribeTopics.add(new MqttTopicSubscription(decodedTopicName.value, subscriptionOption));
		}
		return new Result<>(new MqttSubscribePayload(subscribeTopics), numberOfBytesConsumed);
	}

	private static Result<MqttSubAckPayload> decodeSubAckPayload(ByteBuffer buffer, int bytesRemainingInVariablePart) {
		final List<Integer> grantedQos = new ArrayList<>(bytesRemainingInVariablePart);
		int numberOfBytesConsumed = 0;
		while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
			int reasonCode = ByteBufferUtil.readUnsignedByte(buffer);
			numberOfBytesConsumed++;
			grantedQos.add(reasonCode);
		}
		return new Result<>(new MqttSubAckPayload(grantedQos), numberOfBytesConsumed);
	}

	private static Result<MqttUnsubAckPayload> decodeUnsubAckPayload(ByteBuffer buffer,
																	 int bytesRemainingInVariablePart) {
		final List<Short> reasonCodes = new ArrayList<>(bytesRemainingInVariablePart);
		int numberOfBytesConsumed = 0;
		while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
			short reasonCode = ByteBufferUtil.readUnsignedByte(buffer);
			numberOfBytesConsumed++;
			reasonCodes.add(reasonCode);
		}
		return new Result<>(new MqttUnsubAckPayload(reasonCodes), numberOfBytesConsumed);
	}

	private static Result<MqttUnsubscribePayload> decodeUnsubscribePayload(ByteBuffer buffer, int bytesRemainingInVariablePart) {
		final List<String> unsubscribeTopics = new ArrayList<>();
		int numberOfBytesConsumed = 0;
		while (numberOfBytesConsumed < bytesRemainingInVariablePart) {
			final Result<String> decodedTopicName = decodeString(buffer);
			numberOfBytesConsumed += decodedTopicName.numberOfBytesConsumed;
			unsubscribeTopics.add(decodedTopicName.value);
		}
		return new Result<>(new MqttUnsubscribePayload(unsubscribeTopics), numberOfBytesConsumed);
	}

	private static Result<ByteBuffer> decodePublishPayload(ByteBuffer buffer, int bytesRemainingInVariablePart) {
		byte[] slice = new byte[bytesRemainingInVariablePart];
		buffer.get(slice, 0, bytesRemainingInVariablePart);
		ByteBuffer byteBuffer = ByteBuffer.wrap(slice);
		return new Result<>(byteBuffer, bytesRemainingInVariablePart);
	}

	private static Result<String> decodeString(ByteBuffer buffer) {
		return decodeString(buffer, 0, Integer.MAX_VALUE);
	}

	private static Result<String> decodeString(ByteBuffer buffer, int minBytes, int maxBytes) {
		int size = decodeMsbLsb(buffer);
		int numberOfBytesConsumed = 2;
		if (size < minBytes || size > maxBytes) {
			ByteBufferUtil.skipBytes(buffer, size);
			numberOfBytesConsumed += size;
			return new Result<>(null, numberOfBytesConsumed);
		}
		String s = new String(buffer.array(), buffer.position(), size, StandardCharsets.UTF_8);
		ByteBufferUtil.skipBytes(buffer, size);
		numberOfBytesConsumed += size;
		return new Result<>(s, numberOfBytesConsumed);
	}

	/**
	 * @return the decoded byte[], numberOfBytesConsumed = byte[].length + 2
	 */
	private static byte[] decodeByteArray(ByteBuffer buffer) {
		int size = decodeMsbLsb(buffer);
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		return bytes;
	}

	// packing utils to reduce the amount of garbage while decoding ints
	private static long packInts(int a, int b) {
		return (((long) a) << 32) | (b & 0xFFFFFFFFL);
	}

	private static int unpackA(long ints) {
		return (int) (ints >> 32);
	}

	private static int unpackB(long ints) {
		return (int) ints;
	}

	/**
	 * numberOfBytesConsumed = 2. return decoded result.
	 */
	private static int decodeMsbLsb(ByteBuffer buffer) {
		int min = 0;
		int max = 65535;
		short msbSize = ByteBufferUtil.readUnsignedByte(buffer);
		short lsbSize = ByteBufferUtil.readUnsignedByte(buffer);
		int result = msbSize << 8 | lsbSize;
		if (result < min || result > max) {
			result = -1;
		}
		return result;
	}

	/**
	 * See 1.5.5 Variable Byte Integer section of MQTT 5.0 specification for encoding/decoding rules
	 *
	 * @param buffer the buffer to decode from
	 * @return result pack with a = decoded integer, b = numberOfBytesConsumed. Need to unpack to read them.
	 * @throws DecoderException if bad MQTT protocol limits Remaining Length
	 */
	private static long decodeVariableByteInteger(ByteBuffer buffer) {
		int remainingLength = 0;
		int multiplier = 1;
		short digit;
		int loops = 0;
		do {
			digit = ByteBufferUtil.readUnsignedByte(buffer);
			remainingLength += (digit & 127) * multiplier;
			multiplier *= 128;
			loops++;
		} while ((digit & 128) != 0 && loops < 4);

		if (loops == 4 && (digit & 128) != 0) {
			throw new DecoderException("MQTT protocol limits Remaining Length to 4 bytes");
		}
		return packInts(remainingLength, loops);
	}

	private static final class Result<T> {

		private final T value;
		private final int numberOfBytesConsumed;

		Result(T value, int numberOfBytesConsumed) {
			this.value = value;
			this.numberOfBytesConsumed = numberOfBytesConsumed;
		}
	}

	private static Result<MqttProperties> decodeProperties(ByteBuffer buffer) {
		final long propertiesLength = decodeVariableByteInteger(buffer);
		int totalPropertiesLength = unpackA(propertiesLength);
		int numberOfBytesConsumed = unpackB(propertiesLength);

		MqttProperties decodedProperties = new MqttProperties();
		while (numberOfBytesConsumed < totalPropertiesLength) {
			long propertyId = decodeVariableByteInteger(buffer);
			final int propertyIdValue = unpackA(propertyId);
			numberOfBytesConsumed += unpackB(propertyId);
			MqttProperties.MqttPropertyType propertyType = MqttProperties.MqttPropertyType.valueOf(propertyIdValue);
			switch (propertyType) {
				case PAYLOAD_FORMAT_INDICATOR:
				case REQUEST_PROBLEM_INFORMATION:
				case REQUEST_RESPONSE_INFORMATION:
				case MAXIMUM_QOS:
				case RETAIN_AVAILABLE:
				case WILDCARD_SUBSCRIPTION_AVAILABLE:
				case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
				case SHARED_SUBSCRIPTION_AVAILABLE:
					final int b1 = ByteBufferUtil.readUnsignedByte(buffer);
					numberOfBytesConsumed++;
					decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, b1));
					break;
				case SERVER_KEEP_ALIVE:
				case RECEIVE_MAXIMUM:
				case TOPIC_ALIAS_MAXIMUM:
				case TOPIC_ALIAS:
					final int int2BytesResult = decodeMsbLsb(buffer);
					numberOfBytesConsumed += 2;
					decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, int2BytesResult));
					break;
				case PUBLICATION_EXPIRY_INTERVAL:
				case SESSION_EXPIRY_INTERVAL:
				case WILL_DELAY_INTERVAL:
				case MAXIMUM_PACKET_SIZE:
					final int maxPacketSize = buffer.getInt();
					numberOfBytesConsumed += 4;
					decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, maxPacketSize));
					break;
				case SUBSCRIPTION_IDENTIFIER:
					long vbIntegerResult = decodeVariableByteInteger(buffer);
					numberOfBytesConsumed += unpackB(vbIntegerResult);
					decodedProperties.add(new MqttProperties.IntegerProperty(propertyIdValue, unpackA(vbIntegerResult)));
					break;
				case CONTENT_TYPE:
				case RESPONSE_TOPIC:
				case ASSIGNED_CLIENT_IDENTIFIER:
				case AUTHENTICATION_METHOD:
				case RESPONSE_INFORMATION:
				case SERVER_REFERENCE:
				case REASON_STRING:
					final Result<String> stringResult = decodeString(buffer);
					numberOfBytesConsumed += stringResult.numberOfBytesConsumed;
					decodedProperties.add(new MqttProperties.StringProperty(propertyIdValue, stringResult.value));
					break;
				case USER_PROPERTY:
					final Result<String> keyResult = decodeString(buffer);
					final Result<String> valueResult = decodeString(buffer);
					numberOfBytesConsumed += keyResult.numberOfBytesConsumed;
					numberOfBytesConsumed += valueResult.numberOfBytesConsumed;
					decodedProperties.add(new MqttProperties.UserProperty(keyResult.value, valueResult.value));
					break;
				case CORRELATION_DATA:
				case AUTHENTICATION_DATA:
					final byte[] binaryDataResult = decodeByteArray(buffer);
					numberOfBytesConsumed += binaryDataResult.length + 2;
					decodedProperties.add(new MqttProperties.BinaryProperty(propertyIdValue, binaryDataResult));
					break;
				default:
					//shouldn't reach here
					throw new DecoderException("Unknown property type: " + propertyType);
			}
		}
		return new Result<>(decodedProperties, numberOfBytesConsumed);
	}
}
