# Netty MQTT Client (`netty-mqtt`)

Standalone **MQTT 3.x client** library built on Netty, used by ThingsBoard transport and integrations for device connectivity.

## Features

- TCP/TLS connect with optional username/password and last will
- Subscribe / unsubscribe with per-topic `MqttHandler`
- Publish QoS 0/1/2 with acknowledgement tracking
- Automatic reconnect (`ReconnectStrategy`)
- QoS1/2 retransmission with exponential backoff (`RetransmissionHandler`)
- Idle PING (`MqttPingHandler`)

## Usage

```java
MqttClient client = MqttClient.create(new MqttClientConfig(), defaultHandler, executor);
client.connect("broker.example.com", 1883).addListener(future -> {
    client.on("devices/+/telemetry", (topic, payload) -> { /* ... */ });
    client.publish("devices/me/telemetry", payload, MqttQoS.AT_LEAST_ONCE);
});
```

## Package layout

| Class | Role |
|-------|------|
| `MqttClient` | Public API |
| `MqttClientImpl` | Netty pipeline and state machine |
| `MqttChannelHandler` | Decodes broker MQTT frames |
| `MqttClientConfig` | SSL, credentials, timeouts |

## Protocol reference

No HTTP REST. See [MQTT_API.md](./MQTT_API.md) for MQTT operations this client performs.

## Consumers in ThingsBoard

- `transport/mqtt` and related integrations
- Rule engine / external MQTT clients where Netty pipeline is required
