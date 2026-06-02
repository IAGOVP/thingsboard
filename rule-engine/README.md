# ThingsBoard Rule Engine (`rule-engine`)

Maven aggregator for **rule chain node plugins** executed inside tb-node (and edge) rule engine actors.

## Submodules

| Module | Role |
|--------|------|
| `rule-engine-api` | `TbNode`, `TbContext`, services, `@RuleNode` annotation |
| `rule-engine-components` | Built-in nodes: filter, transform, action, external integrations |

## Runtime (no HTTP in this module)

Rule nodes do **not** expose REST. They are invoked by the server when `TbMsg` flows through a rule chain:

```
RuleEngineActor → TbNode.onMsg(TbContext, TbMsg) → ctx.tellSuccess / tellNext / tellFailure
```

See [API.md](./API.md) and node catalog [RULE_NODES.md](./RULE_NODES.md).

## Key types

| Type | Purpose |
|------|---------|
| `TbNode` | `init`, `onMsg`, `destroy` lifecycle |
| `TbContext` | Routing + DAO + script engine access |
| `@RuleNode` | Registers node in UI (name, type, config class) |
| `Tb*Node` | Concrete implementations (~74 nodes) |

## External calls from nodes

Some nodes call **outbound** HTTP/MQTT/Kafka (e.g. `TbRestApiCallNode`, `TbMqttNode`). Those are documented per node in [RULE_NODES.md](./RULE_NODES.md).

## Related

- Server actors: `application/.../actors/ruleChain/`
- User docs: https://thingsboard.io/docs/user-guide/rule-engine-2-0/
