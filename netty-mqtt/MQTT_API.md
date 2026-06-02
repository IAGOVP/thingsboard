# Netty MQTT — protocol API

This module implements an **MQTT 3.1 / 3.1.1 client** (Netty codec). There is no HTTP server.

## Client API (`MqttClient`)

| Method | MQTT action | Description |
|--------|-------------|-------------|
| `connect(host)` / `connect(host, port)` | CONNECT → CONNACK | Opens TCP (or TLS) and completes handshake |
| `reconnect()` | CONNECT | Reconnects to last host/port |
| `disconnect()` | DISCONNECT | Closes session and channel |
| `on(topic, handler)` | SUBSCRIBE → SUBACK | Persistent subscription (default QoS AT_LEAST_ONCE) |
| `on(topic, handler, qos)` | SUBSCRIBE | Subscription with explicit QoS |
| `once(topic, handler)` | SUBSCRIBE | Auto-unsubscribe after first message |
| `off(topic)` / `off(topic, handler)` | UNSUBSCRIBE → UNSUBACK | Remove subscription(s) |
| `publish(topic, payload)` | PUBLISH | QoS 0 (fire-and-forget) |
| `publish(topic, payload, qos)` | PUBLISH → PUBACK / PUBREC… | QoS 1 or 2 with ack tracking |
| `publish(..., retain)` | PUBLISH | Optional retain flag |
| `isConnected()` | — | Channel active and CONNACK succeeded |
| `setCallback(callback)` | — | Connection lost / reconnect hooks |

Factory: `MqttClient.create(config, defaultHandler, handlerExecutor)`.

## Inbound frames (`MqttChannelHandler`)

| MQTT type | Handler method | Effect |
|-----------|----------------|--------|
| CONNACK | `handleConack` | Completes connect promise; resubscribes on reconnect |
| SUBACK | `handleSubAck` | Completes pending subscribe |
| UNSUBACK | `handleUnsuback` | Completes pending unsubscribe |
| PUBLISH | `handlePublish` | Routes to topic handlers; QoS2 starts PUBREC flow |
| PUBACK | `handlePuback` | Completes QoS1 publish |
| PUBREC / PUBREL / PUBCOMP | `handlePubrec` … | QoS2 publish and receive handshakes |
| DISCONNECT | `handleDisconnect` | Closes client |

## Configuration (`MqttClientConfig`)

| Setting | Purpose |
|---------|---------|
| `sslContext` | TLS for `ssl://` brokers |
| `clientId`, `username`, `password` | CONNECT payload |
| `lastWill` | CONNECT last will |
| `cleanSession`, `protocolVersion` | Session flags |
| `timeoutSeconds` | Connect / idle timeout |
| `reconnect`, `reconnectDelay` | Auto-reconnect toggle and base delay |
| `retransmissionConfig` | Max attempts and backoff for unacked messages |
| `maxBytesInMessage` | Decoder size limit |

## Callbacks

| Type | When invoked |
|------|----------------|
| `MqttHandler.onMessage` | Matching PUBLISH received |
| `MqttClientCallback.connectionLost` | Channel closed unexpectedly |
| `MqttClientCallback.onSuccessfulReconnect` | Reconnect and resubscribe completed |
