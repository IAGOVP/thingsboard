# HTTP device transport API

Implemented by `DeviceApiController` in `common/transport/http`.  
Base path: `/api/v1` — authenticate with **device access token** in the URL path (`deviceToken`).

| Method | Path | Summary |
|--------|------|---------|
| GET | `/api/v1/{deviceToken}/attributes` | Read client/shared attributes (`clientKeys`, `sharedKeys` query params) |
| POST | `/api/v1/{deviceToken}/attributes` | Post client attribute updates (JSON body) |
| POST | `/api/v1/{deviceToken}/telemetry` | Post time-series telemetry (JSON object or array) |
| POST | `/api/v1/{deviceToken}/claim` | Submit device claiming information |
| GET | `/api/v1/{deviceToken}/rpc` | Long-poll for server-side RPC to device (`timeout` ms) |
| POST | `/api/v1/{deviceToken}/rpc` | Send two-way RPC request to server (JSON `method` + `params`) |
| POST | `/api/v1/{deviceToken}/rpc/{requestId}` | Reply to server-side RPC |
| GET | `/api/v1/{deviceToken}/attributes/updates` | Long-poll for shared attribute updates |
| GET | `/api/v1/{deviceToken}/firmware` | Download firmware chunk (OTA) |
| GET | `/api/v1/{deviceToken}/software` | Download software package chunk (OTA) |
| POST | `/api/v1/provision` | Device provisioning (no token in path) |

Responses use `DeferredResult` for async completion. Security: `TransportSecurityConfiguration` permits `/api/v1/**`; validation is token-based per request.

Official reference: [HTTP Device API](https://thingsboard.io/docs/reference/http-api/).
