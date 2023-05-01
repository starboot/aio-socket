package cn.starboot.mqtt.codec;

import cn.starboot.mqtt.codec.MqttProperties.MqttPropertyType;
import cn.starboot.socket.codec.util.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * mqtt 消息构造器
 *
 * @author netty
 */
public final class MqttMessageBuilders {

	public static final class PublishBuilder {
		private String topic;
		private boolean retained;
		private MqttQoS qos;
		private ByteBuffer payload;
		private int messageId;
		private MqttProperties mqttProperties;

		PublishBuilder() {
		}

		public PublishBuilder topicName(String topic) {
			this.topic = topic;
			return this;
		}

		public PublishBuilder retained(boolean retained) {
			this.retained = retained;
			return this;
		}

		public PublishBuilder qos(MqttQoS qos) {
			this.qos = qos;
			return this;
		}

		public PublishBuilder payload(ByteBuffer payload) {
			this.payload = payload;
			return this;
		}

		public PublishBuilder messageId(int messageId) {
			this.messageId = messageId;
			return this;
		}

		public PublishBuilder properties(MqttProperties properties) {
			this.mqttProperties = properties;
			return this;
		}

		public boolean isRetained() {
			return retained;
		}

		public MqttPublishMessage build() {
			MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, qos, retained, 0);
			MqttPublishVariableHeader mqttVariableHeader =
					new MqttPublishVariableHeader(topic, messageId, mqttProperties);
			return new MqttPublishMessage(mqttFixedHeader, mqttVariableHeader, ByteBufferUtil.clone(payload));
		}
	}

	public static final class ConnectBuilder {

		private MqttVersion version = MqttVersion.MQTT_3_1_1;
		private String clientId;
		private boolean cleanSession;
		private boolean hasUser;
		private boolean hasPassword;
		private int keepAliveSecs;
		private MqttProperties willProperties = MqttProperties.NO_PROPERTIES;
		private boolean willFlag;
		private boolean willRetain;
		private MqttQoS willQos = MqttQoS.AT_MOST_ONCE;
		private String willTopic;
		private byte[] willMessage;
		private String username;
		private byte[] password;
		private MqttProperties properties = MqttProperties.NO_PROPERTIES;

		ConnectBuilder() {
		}

		public ConnectBuilder protocolVersion(MqttVersion version) {
			this.version = version;
			return this;
		}

		public ConnectBuilder clientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public ConnectBuilder cleanSession(boolean cleanSession) {
			this.cleanSession = cleanSession;
			return this;
		}

		public ConnectBuilder keepAlive(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public ConnectBuilder willFlag(boolean willFlag) {
			this.willFlag = willFlag;
			return this;
		}

		public ConnectBuilder willQoS(MqttQoS willQos) {
			this.willQos = willQos;
			return this;
		}

		public ConnectBuilder willTopic(String willTopic) {
			this.willTopic = willTopic;
			return this;
		}

		public ConnectBuilder willMessage(byte[] willMessage) {
			this.willMessage = willMessage;
			return this;
		}

		public ConnectBuilder willRetain(boolean willRetain) {
			this.willRetain = willRetain;
			return this;
		}

		public ConnectBuilder willProperties(MqttProperties willProperties) {
			this.willProperties = willProperties;
			return this;
		}

		public ConnectBuilder hasUser(boolean value) {
			this.hasUser = value;
			return this;
		}

		public ConnectBuilder hasPassword(boolean value) {
			this.hasPassword = value;
			return this;
		}

		public ConnectBuilder username(String username) {
			this.hasUser = username != null;
			this.username = username;
			return this;
		}

		public ConnectBuilder password(byte[] password) {
			this.hasPassword = password != null;
			this.password = password;
			return this;
		}

		public ConnectBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public MqttConnectMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttConnectVariableHeader mqttConnectVariableHeader =
					new MqttConnectVariableHeader(
							version.protocolName(),
							version.protocolLevel(),
							hasUser,
							hasPassword,
							willRetain,
							willQos.value(),
							willFlag,
							cleanSession,
							keepAliveSecs,
							properties);
			MqttConnectPayload mqttConnectPayload =
					new MqttConnectPayload(clientId, willProperties, willTopic, willMessage, username, password);
			return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
		}
	}

	public static final class SubscribeBuilder {

		private final List<MqttTopicSubscription> subscriptions;
		private int messageId;
		private MqttProperties properties;

		SubscribeBuilder() {
			subscriptions = new ArrayList<>(5);
		}

		public SubscribeBuilder addSubscription(MqttTopicSubscription subscription) {
			subscriptions.add(subscription);
			return this;
		}

		public SubscribeBuilder addSubscription(MqttQoS qos, String topic) {
			return addSubscription(new MqttTopicSubscription(topic, qos));
		}

		public SubscribeBuilder addSubscription(String topic, MqttSubscriptionOption option) {
			return addSubscription(new MqttTopicSubscription(topic, option));
		}

		public SubscribeBuilder addSubscriptions(Collection<MqttTopicSubscription> subscriptionColl) {
			subscriptions.addAll(subscriptionColl);
			return this;
		}

		public SubscribeBuilder messageId(int messageId) {
			this.messageId = messageId;
			return this;
		}

		public SubscribeBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public MqttSubscribeMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
			MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader =
					new MqttMessageIdAndPropertiesVariableHeader(messageId, properties);
			MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(subscriptions);
			return new MqttSubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
		}
	}

	public static final class UnsubscribeBuilder {
		private final List<String> topicFilters;
		private int messageId;
		private MqttProperties properties;

		UnsubscribeBuilder() {
			topicFilters = new ArrayList<>(5);
		}

		public UnsubscribeBuilder addTopicFilter(String topic) {
			topicFilters.add(topic);
			return this;
		}

		public UnsubscribeBuilder addTopicFilters(Collection<String> topicColl) {
			topicFilters.addAll(topicColl);
			return this;
		}

		public UnsubscribeBuilder messageId(int messageId) {
			this.messageId = messageId;
			return this;
		}

		public UnsubscribeBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public MqttUnsubscribeMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
			MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader =
					new MqttMessageIdAndPropertiesVariableHeader(messageId, properties);
			MqttUnsubscribePayload mqttSubscribePayload = new MqttUnsubscribePayload(topicFilters);
			return new MqttUnsubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
		}
	}

	public interface PropertiesInitializer<T> {
		void apply(T builder);
	}

	public static final class ConnAckBuilder {

		private MqttConnectReasonCode returnCode;
		private boolean sessionPresent;
		private MqttProperties properties = MqttProperties.NO_PROPERTIES;
		private ConnAckPropertiesBuilder propsBuilder;

		private ConnAckBuilder() {
		}

		public ConnAckBuilder returnCode(MqttConnectReasonCode returnCode) {
			this.returnCode = returnCode;
			return this;
		}

		public ConnAckBuilder sessionPresent(boolean sessionPresent) {
			this.sessionPresent = sessionPresent;
			return this;
		}

		public ConnAckBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public ConnAckBuilder properties(PropertiesInitializer<ConnAckPropertiesBuilder> consumer) {
			if (propsBuilder == null) {
				propsBuilder = new ConnAckPropertiesBuilder();
			}
			consumer.apply(propsBuilder);
			return this;
		}

		public MqttConnAckMessage build() {
			if (propsBuilder != null) {
				properties = propsBuilder.build();
			}
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttConnAckVariableHeader mqttConnAckVariableHeader =
					new MqttConnAckVariableHeader(returnCode, sessionPresent, properties);
			return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
		}
	}

	public static final class ConnAckPropertiesBuilder {
		private String clientId;
		private Long sessionExpiryInterval;
		private int receiveMaximum;
		private Byte maximumQos;
		private boolean retain;
		private Long maximumPacketSize;
		private int topicAliasMaximum;
		private String reasonString;
		private final MqttProperties.UserProperties userProperties = new MqttProperties.UserProperties();
		private Boolean wildcardSubscriptionAvailable;
		private Boolean subscriptionIdentifiersAvailable;
		private Boolean sharedSubscriptionAvailable;
		private Integer serverKeepAlive;
		private String responseInformation;
		private String serverReference;
		private String authenticationMethod;
		private byte[] authenticationData;

		public MqttProperties build() {
			final MqttProperties props = new MqttProperties();
			if (clientId != null) {
				props.add(new MqttProperties.StringProperty(MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER.value(),
						clientId));
			}
			if (sessionExpiryInterval != null) {
				props.add(new MqttProperties.IntegerProperty(
						MqttPropertyType.SESSION_EXPIRY_INTERVAL.value(), sessionExpiryInterval.intValue()));
			}
			if (receiveMaximum > 0) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.RECEIVE_MAXIMUM.value(), receiveMaximum));
			}
			if (maximumQos != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.MAXIMUM_QOS.value(), receiveMaximum));
			}
			props.add(new MqttProperties.IntegerProperty(MqttPropertyType.RETAIN_AVAILABLE.value(), retain ? 1 : 0));
			if (maximumPacketSize != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.MAXIMUM_PACKET_SIZE.value(),
						maximumPacketSize.intValue()));
			}
			props.add(new MqttProperties.IntegerProperty(MqttPropertyType.TOPIC_ALIAS_MAXIMUM.value(),
					topicAliasMaximum));
			if (reasonString != null) {
				props.add(new MqttProperties.StringProperty(MqttPropertyType.REASON_STRING.value(), reasonString));
			}
			props.add(userProperties);
			if (wildcardSubscriptionAvailable != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE.value(),
						wildcardSubscriptionAvailable ? 1 : 0));
			}
			if (subscriptionIdentifiersAvailable != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE.value(),
						subscriptionIdentifiersAvailable ? 1 : 0));
			}
			if (sharedSubscriptionAvailable != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE.value(),
						sharedSubscriptionAvailable ? 1 : 0));
			}
			if (serverKeepAlive != null) {
				props.add(new MqttProperties.IntegerProperty(MqttPropertyType.SERVER_KEEP_ALIVE.value(),
						serverKeepAlive));
			}
			if (responseInformation != null) {
				props.add(new MqttProperties.StringProperty(MqttPropertyType.RESPONSE_INFORMATION.value(),
						responseInformation));
			}
			if (serverReference != null) {
				props.add(new MqttProperties.StringProperty(MqttPropertyType.SERVER_REFERENCE.value(),
						serverReference));
			}
			if (authenticationMethod != null) {
				props.add(new MqttProperties.StringProperty(MqttPropertyType.AUTHENTICATION_METHOD.value(),
						authenticationMethod));
			}
			if (authenticationData != null) {
				props.add(new MqttProperties.BinaryProperty(MqttPropertyType.AUTHENTICATION_DATA.value(),
						authenticationData));
			}
			return props;
		}

		public ConnAckPropertiesBuilder sessionExpiryInterval(long seconds) {
			this.sessionExpiryInterval = seconds;
			return this;
		}

		public ConnAckPropertiesBuilder receiveMaximum(int value) {
			if (value <= 0) {
				throw new IllegalArgumentException("receive maximum property must be > 0");
			}
			this.receiveMaximum = value;
			return this;
		}

		public ConnAckPropertiesBuilder maximumQos(byte value) {
			if (value != 0 && value != 1) {
				throw new IllegalArgumentException("maximum QoS property could be 0 or 1");
			}
			this.maximumQos = value;
			return this;
		}

		public ConnAckPropertiesBuilder retainAvailable(boolean retain) {
			this.retain = retain;
			return this;
		}

		public ConnAckPropertiesBuilder maximumPacketSize(long size) {
			if (size <= 0) {
				throw new IllegalArgumentException("maximum packet size property must be > 0");
			}
			this.maximumPacketSize = size;
			return this;
		}

		public ConnAckPropertiesBuilder assignedClientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public ConnAckPropertiesBuilder topicAliasMaximum(int value) {
			this.topicAliasMaximum = value;
			return this;
		}

		public ConnAckPropertiesBuilder reasonString(String reason) {
			this.reasonString = reason;
			return this;
		}

		public ConnAckPropertiesBuilder userProperty(String name, String value) {
			userProperties.add(name, value);
			return this;
		}

		public ConnAckPropertiesBuilder wildcardSubscriptionAvailable(boolean value) {
			this.wildcardSubscriptionAvailable = value;
			return this;
		}

		public ConnAckPropertiesBuilder subscriptionIdentifiersAvailable(boolean value) {
			this.subscriptionIdentifiersAvailable = value;
			return this;
		}

		public ConnAckPropertiesBuilder sharedSubscriptionAvailable(boolean value) {
			this.sharedSubscriptionAvailable = value;
			return this;
		}

		public ConnAckPropertiesBuilder serverKeepAlive(int seconds) {
			this.serverKeepAlive = seconds;
			return this;
		}

		public ConnAckPropertiesBuilder responseInformation(String value) {
			this.responseInformation = value;
			return this;
		}

		public ConnAckPropertiesBuilder serverReference(String host) {
			this.serverReference = host;
			return this;
		}

		public ConnAckPropertiesBuilder authenticationMethod(String methodName) {
			this.authenticationMethod = methodName;
			return this;
		}

		public ConnAckPropertiesBuilder authenticationData(byte[] rawData) {
			this.authenticationData = rawData.clone();
			return this;
		}
	}

	public static final class PubAckBuilder {
		private int packetId;
		private byte reasonCode;
		private MqttProperties properties;

		PubAckBuilder() {
		}

		public PubAckBuilder reasonCode(byte reasonCode) {
			this.reasonCode = reasonCode;
			return this;
		}

		public PubAckBuilder packetId(int packetId) {
			this.packetId = packetId;
			return this;
		}

		public PubAckBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public MqttMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttPubReplyMessageVariableHeader mqttPubAckVariableHeader =
					new MqttPubReplyMessageVariableHeader(packetId, reasonCode, properties);
			return new MqttMessage(mqttFixedHeader, mqttPubAckVariableHeader);
		}
	}

	public static final class SubAckBuilder {
		private int packetId;
		private MqttProperties properties;
		private final List<MqttQoS> grantedQosList;

		SubAckBuilder() {
			grantedQosList = new ArrayList<>();
		}

		public SubAckBuilder packetId(int packetId) {
			this.packetId = packetId;
			return this;
		}

		public SubAckBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public SubAckBuilder addGrantedQos(MqttQoS qos) {
			this.grantedQosList.add(qos);
			return this;
		}

		public SubAckBuilder addGrantedQoses(MqttQoS... qoses) {
			this.grantedQosList.addAll(Arrays.asList(qoses));
			return this;
		}

		public SubAckBuilder addGrantedQosList(List<MqttQoS> qosList) {
			this.grantedQosList.addAll(qosList);
			return this;
		}

		public MqttSubAckMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttMessageIdAndPropertiesVariableHeader mqttSubAckVariableHeader =
					new MqttMessageIdAndPropertiesVariableHeader(packetId, properties);

			//transform to primitive types
			int[] grantedQosArray = new int[this.grantedQosList.size()];
			int i = 0;
			for (MqttQoS grantedQos : this.grantedQosList) {
				grantedQosArray[i++] = grantedQos.value();
			}

			MqttSubAckPayload subAckPayload = new MqttSubAckPayload(grantedQosArray);
			return new MqttSubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload);
		}
	}

	public static final class UnsubAckBuilder {
		private int packetId;
		private MqttProperties properties;
		private final List<Short> reasonCodes = new ArrayList<>();

		UnsubAckBuilder() {
		}

		public UnsubAckBuilder packetId(int packetId) {
			this.packetId = packetId;
			return this;
		}

		public UnsubAckBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public UnsubAckBuilder addReasonCode(short reasonCode) {
			this.reasonCodes.add(reasonCode);
			return this;
		}

		public UnsubAckBuilder addReasonCodes(Short... reasonCodes) {
			this.reasonCodes.addAll(Arrays.asList(reasonCodes));
			return this;
		}

		public MqttUnsubAckMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttMessageIdAndPropertiesVariableHeader mqttSubAckVariableHeader =
					new MqttMessageIdAndPropertiesVariableHeader(packetId, properties);

			MqttUnsubAckPayload subAckPayload = new MqttUnsubAckPayload(reasonCodes);
			return new MqttUnsubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload);
		}
	}

	public static final class DisconnectBuilder {

		private MqttProperties properties;
		private byte reasonCode;

		DisconnectBuilder() {
		}

		public DisconnectBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public DisconnectBuilder reasonCode(byte reasonCode) {
			this.reasonCode = reasonCode;
			return this;
		}

		public MqttMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttReasonCodeAndPropertiesVariableHeader mqttDisconnectVariableHeader =
					new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties);

			return new MqttMessage(mqttFixedHeader, mqttDisconnectVariableHeader);
		}
	}

	public static final class AuthBuilder {

		private MqttProperties properties;
		private byte reasonCode;

		AuthBuilder() {
		}

		public AuthBuilder properties(MqttProperties properties) {
			this.properties = properties;
			return this;
		}

		public AuthBuilder reasonCode(byte reasonCode) {
			this.reasonCode = reasonCode;
			return this;
		}

		public MqttMessage build() {
			MqttFixedHeader mqttFixedHeader =
					new MqttFixedHeader(MqttMessageType.AUTH, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttReasonCodeAndPropertiesVariableHeader mqttAuthVariableHeader =
					new MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties);

			return new MqttMessage(mqttFixedHeader, mqttAuthVariableHeader);
		}
	}

	public static ConnectBuilder connect() {
		return new ConnectBuilder();
	}

	public static ConnAckBuilder connAck() {
		return new ConnAckBuilder();
	}

	public static PublishBuilder publish() {
		return new PublishBuilder();
	}

	public static SubscribeBuilder subscribe() {
		return new SubscribeBuilder();
	}

	public static UnsubscribeBuilder unsubscribe() {
		return new UnsubscribeBuilder();
	}

	public static PubAckBuilder pubAck() {
		return new PubAckBuilder();
	}

	public static SubAckBuilder subAck() {
		return new SubAckBuilder();
	}

	public static UnsubAckBuilder unsubAck() {
		return new UnsubAckBuilder();
	}

	public static DisconnectBuilder disconnect() {
		return new DisconnectBuilder();
	}

	public static AuthBuilder auth() {
		return new AuthBuilder();
	}

	private MqttMessageBuilders() {
	}
}
