# `tb-node` Docker image

Packages the **ThingsBoard core** JAR from the `application` module as the `tb-node` container used in microservices compose.

- Pulls `.deb` from `application` build output
- Config: `docker/logback.xml`, env files under `../../docker/tb-node*.env`
- Main class: `org.thingsboard.server.ThingsboardServerApplication`

REST API is identical to the monolith; see [../../docs/REST_API.md](../../docs/REST_API.md).
