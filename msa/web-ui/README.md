# `tb-web-ui` microservice

Node/Express server that hosts the compiled Angular UI (`ui-ngx` build output).

- Entry: `server.ts`
- Config: `config/default.yml`, `config/custom-environment-variables.yml`
- Docker: `docker/Dockerfile`, `docker/start-web-ui.sh`

When `thingsboard.enableProxy` is true, `/api/*` and WebSockets are forwarded to the core node.
