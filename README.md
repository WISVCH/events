# Events [![Maintainability](https://api.codeclimate.com/v1/badges/f73308e49963d9782643/maintainability)](https://codeclimate.com/github/WISVCH/events/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/f73308e49963d9782643/test_coverage)](https://codeclimate.com/github/WISVCH/events/test_coverage)

Event registration for W.I.S.V. 'Christiaan Huygens'.

Uses [WISVCH/bootstrap-theme](https://github.com/WISVCH/bootstrap-theme) for styling.

## Documentation

All the documentation is on [https://wisvch.github.io/events/](https://wisvch.github.io/events/).

## Development

You can work on Events in three different ways:

- **Dev Container** (containerized workspace + dependencies).
- **Bare metal development with services in Docker** (local IDE + `docker compose` dependencies).
- **All bare metal** (manual local setup of all dependencies).

### Option 1 – Dev Container (VS Code or IntelliJ)

1. **Prerequisites**
   - Docker Desktop (Linux users: Docker Engine + Docker Compose v2).
   - For VS Code: install the *Dev Containers* extension.  
     For IntelliJ IDEA: install JetBrains Gateway with the *Dev Containers* plugin.
2. **Open the workspace**
   - VS Code: `Dev Containers: Rebuild and Reopen in Container`.
   - IntelliJ: `File ▸ Open… ▸ .devcontainer/devcontainer.json` via Gateway.
3. The dev container boots the project plus all dependencies using the included Compose files. No local tooling besides Docker is required.
4. **Run the application** with the supplied run configurations:
   - IntelliJ: select `Application [devcontainer]` in the dropdown at the top-right, then click the green play button.
   - VS Code: `Run and Debug ▸ Application [devcontainer]` (Not `Application [dev]`).

While the container is running:

| Service | Host URL / Port | Notes |
| --- | --- | --- |
| Events app | http://localhost:3090/events | Main web app |
| PostgreSQL 15 | localhost:35433 | Primary database (`postgres/postgres`) |
| pgAdmin | http://localhost:3091 | `admin@example.com` / `admin` |
| Mailcatcher UI | http://localhost:3092 | Test inbox |
| Mailcatcher SMTP | localhost:3588 | SMTP endpoint for dev mail |
| Mock OIDC | http://localhost:3093 | Login provider for local auth flow |

The mock OIDC service includes two ready-made accounts:

| Username | Password | Roles |
| --- | --- | --- |
| `constantijn` | `pwd` | Admin (beheer) |
| `christiaan` | `pwd` | Standard user |

### Option 2 – Docker Compose Dependencies (Local IDE)

1. Install JDK 17, Docker Desktop/Compose, and your preferred editor.
2. Start the shared services:

   ```bash
   docker compose up -d db pgadmin mailcatcher oidc
   ```

   (Stop them with `docker compose down` when you are done.)
3. Open the project in VS Code or IntelliJ. The project is already Gradle based, so it will import automatically.
4. Run the backend with the supplied run configurations:
   - IntelliJ: `Run ▸ Run 'Application [dev]'`.
   - VS Code: `Run and Debug ▸ Application [dev]`.

All services listen on the same host ports listed in the table above.

### Option 3 – Manual Setup

Configure everything yourself only if you cannot run Docker:

1. **PostgreSQL** – create a database named `events` reachable via `localhost:35433` (or update the connection in `config/application-dev.yml`).
2. **Mail** – run a local SMTP server or update `spring.mail.*` in `config/application-dev.yml` with your provider.
3. **OIDC provider** – register a Keycloak or other OIDC issuer and update:

   ```yaml
   spring:
     security:
       oauth2:
         client:
           registration:
             wisvchconnect:
               client-id: <client id>
               client-secret: <client secret>
           provider:
             wisvchconnect:
               issuer-uri: https://login.ch.tudelft.nl/realms/wisvch
   ```

4. **Mollie/CH Pay** – set `mollie.apikey`, and update `wisvch.chpay.*` endpoints/keys as needed.
5. **Base URL** – match `server.port` (usually `3090`) and `server.servlet.context-path` (`/events`).
6. Once the configuration is in place, build and run with:

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

   or reuse the IDE run configurations.

## Contribution

If you want to contribute, awesome! First, pick an issue and self-assign it. Make your changes in a new branch, with the following naming convention:

Fixing a bug? > "fix_short-description-of-bug"  
Implementing a new feature? > "feature_short-description-of-feature"

Once you're satisfied with your changes, create a pull request and give it the label "ready for inspection". You can assign any of the maintainers 
in specific or wait for someone to pick it up. Make sure to include tests and documentation.

Got stuck with your implementation? Create a pull request with an explanation and give it the label "help wanted".
