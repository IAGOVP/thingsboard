# ThingsBoard REST Client (`rest-client`)

Java library that wraps **ThingsBoard HTTP REST API** for external tools, tests, monitoring, and integrations.

## Contents

| File | Role |
|------|------|
| `RestClient.java` | ~400+ methods mapping to `/api/**` endpoints |
| `utils/RestJsonConverter.java` | JSON → `AttributeKvEntry` / `TsKvEntry` conversion |

## Authentication

```java
RestClient client = new RestClient("https://demo.thingsboard.io");
client.login("tenant@thingsboard.org", "tenant");
// or
RestClient apiKeyClient = RestClient.withApiKey("https://...", "<api-key>");
```

- **JWT**: `X-Authorization: Bearer &lt;token&gt;` with auto-refresh via `/api/auth/token`
- **API key**: `X-Authorization: ApiKey &lt;key&gt;`

## Endpoint documentation

- Grouped overview: [HTTP_ENDPOINTS.md](./HTTP_ENDPOINTS.md)
- Full path list used by this client: [API_PATHS.txt](./API_PATHS.txt) (334 templates)
- Server API reference: [../docs/REST_API.md](../docs/REST_API.md)

## Consumers

- `monitoring` — `TbClient extends RestClient`
- Black-box / integration tests
- Rule engine test utilities
