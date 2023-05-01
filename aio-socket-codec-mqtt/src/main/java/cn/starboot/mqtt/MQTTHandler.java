package cn.starboot.mqtt;

import cn.starboot.mqtt.codec.MqttDecoder;
import cn.starboot.mqtt.codec.MqttEncoder;
import cn.starboot.mqtt.codec.MqttMessage;
import cn.starboot.socket.Packet;
import cn.starboot.socket.ProtocolEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import static cn.starboot.mqtt.codec.MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;

/**
 * aio-socket mqtt抽象处理器
 *
 * @author MDong
 */
public abstract class MQTTHandler implements AioHandler {

	private final int maxBytesInMessage;

	private final int maxClientIdLength;

	private final MqttDecoder mqttDecoder;

	private final MqttEncoder mqttEncoder;

	public MQTTHandler() {
		this.maxBytesInMessage = DEFAULT_MAX_BYTES_IN_MESSAGE;
		this.maxClientIdLength = DEFAULT_MAX_BYTES_IN_MESSAGE;
		this.mqttDecoder = new MqttDecoder();
		this.mqttEncoder = MqttEncoder.getInstance();
	}

	public MQTTHandler(int maxBytesInMessage) {
		this.maxBytesInMessage = maxBytesInMessage;
		this.maxClientIdLength = DEFAULT_MAX_BYTES_IN_MESSAGE;
		this.mqttDecoder = new MqttDecoder(this.maxBytesInMessage);
		this.mqttEncoder = MqttEncoder.getInstance();
	}

	public MQTTHandler(int maxBytesInMessage, int maxClientIdLength) {
		this.maxBytesInMessage = maxBytesInMessage;
		this.maxClientIdLength = maxClientIdLength;
		this.mqttDecoder = new MqttDecoder(this.maxBytesInMessage, this.maxClientIdLength);
		this.mqttEncoder = MqttEncoder.getInstance();
	}

	@Override
	public Packet handle(ChannelContext channelContext, Packet packet) {
		if (packet instanceof MqttMessage) {
			return handle(channelContext, (MqttMessage) packet);
		}
		return null;
	}

	@Override
	public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
		return mqttDecoder.doDecode(channelContext, readBuffer);
	}

	@Override
	public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		if (packet instanceof MqttMessage) {
			mqttEncoder.doEncode(channelContext, (MqttMessage) packet);
		} else {
			throw new AioEncoderException("Mqtt Protocol encode failed because of the Packet is not MqttMessage.");
		}

	}

	@Override
	public ProtocolEnum name() {
		return ProtocolEnum.MQTT_v5_0;
	}

	public abstract Packet handle(ChannelContext channelContext, MqttMessage mqttMessage);
}
