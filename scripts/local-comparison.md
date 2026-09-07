# Local master-versus-upgrade comparison

This starts two isolated PostgreSQL 15 databases and two Events instances:

| Instance | Database | Database port | App port |
| --- | --- | --- | --- |
| `master` | `events_master` | `5433` | `3050` |
| `upgrade` | `events_upgrade` | `5434` | `3090` |

The PostgreSQL containers are stopped at the end of the day, not removed, so their data persists.

## First-time setup

```sh
git worktree add /private/tmp/events-master master

docker run -d --name events-master-postgres \
  -e POSTGRES_DB=events_master \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 postgres:15

docker run -d --name events-upgrade-postgres \
  -e POSTGRES_DB=events_upgrade \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5434:5432 postgres:15

mkdir -p /private/tmp/events-comparison
```

Create `/private/tmp/events-comparison/master.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/events_master
    username: postgres
    password: postgres
  jpa.hibernate.ddl-auto: validate
  flyway.enabled: false
server:
  port: 3050
  forward-headers-strategy: framework
wisvch.events.image.path: http://localhost:3050/events/api/v1/documents/
wisvch.chpay.clientUri: http://localhost:3050/events
mollie.clientUri: http://localhost:3050/events
```

Create `/private/tmp/events-comparison/upgrade.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/events_upgrade
    username: postgres
    password: postgres
  jpa.hibernate.ddl-auto: validate
  flyway.enabled: false
server:
  port: 3090
  forward-headers-strategy: framework
wisvch.events.image.path: https://javelin-chief-moose.ngrok-free.app/events/api/v1/documents/
wisvch.chpay.clientUri: https://javelin-chief-moose.ngrok-free.app/events
mollie.clientUri: https://javelin-chief-moose.ngrok-free.app/events
```

`ddl-auto: validate` and `flyway.enabled: false` ensure restarts neither recreate nor refill the databases.

## Start or resume

```sh
docker start events-master-postgres events-upgrade-postgres

(cd /private/tmp/events-master && ./gradlew bootJar --console=plain)
./gradlew bootJar --console=plain

screen -dmS events-master /bin/zsh -lc \
  'exec /usr/bin/env SPRING_CONFIG_ADDITIONAL_LOCATION=file:/private/tmp/events-comparison/master.yml /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java -jar /private/tmp/events-master/build/libs/events-master.jar >> /private/tmp/events-comparison/master.log 2>&1'

screen -dmS events-upgrade /bin/zsh -lc \
  'exec /usr/bin/env SPRING_CONFIG_ADDITIONAL_LOCATION=file:/private/tmp/events-comparison/upgrade.yml /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java -jar build/libs/events.jar >> /private/tmp/events-comparison/upgrade.log 2>&1'
```

For the upgrade instance, start `ngrok` yourself for port `3090` when its public callback URL is needed.

Follow the logs:

```sh
tail -f /private/tmp/events-comparison/master.log
tail -f /private/tmp/events-comparison/upgrade.log
```

## Reset both databases to the same demo fixture

```sh
docker cp scripts/seed-local-demo-data.sql events-master-postgres:/tmp/seed-local-demo-data.sql
docker cp scripts/seed-local-demo-data.sql events-upgrade-postgres:/tmp/seed-local-demo-data.sql

docker exec events-master-postgres psql -v ON_ERROR_STOP=1 -U postgres -d events_master -f /tmp/seed-local-demo-data.sql
docker exec events-upgrade-postgres psql -v ON_ERROR_STOP=1 -U postgres -d events_upgrade -f /tmp/seed-local-demo-data.sql
```

## Shut down for the day

```sh
screen -S events-master -X quit
screen -S events-upgrade -X quit
docker stop events-master-postgres events-upgrade-postgres
```

If `master` was started using the old pipe-to-`tee` command rather than the `screen` command above, stop its Java process instead:

```sh
lsof -tiTCP:3050 -sTCP:LISTEN | xargs kill -TERM
```
