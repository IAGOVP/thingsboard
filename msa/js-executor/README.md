# `tb-js-executor` microservice

Executes user JavaScript for rule nodes and transformers in an isolated (sandbox) or fast (non-sandbox) mode.

- Entry: `server.ts`
- Kafka consumer/producer: `queue/kafkaTemplate.ts`
- Request handler: `api/jsInvokeMessageProcessor.ts`
- Health: `GET /livenessProbe` on `http_port`

Messages: compile / invoke / release (see `api/jsExecutor.models.ts`).
