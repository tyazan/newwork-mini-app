# Architecture

## Style
**Modular Monolith**: a single deployable unit with clear internal boundaries so modules can be extracted to services later.

## Backend Modules
- **hrprofile**: employee profile read/update; field-level redaction for sensitive attributes
- **feedback**: add/list feedback; optional AI polish
- **absence**: create absence request (REQUESTED)
- **ai**: TextPolisher port with two adapters:
1. OpenAIPolisher (remote; uses OpenAI Chat Completions)
2. LocalPolisher (deterministic fallback; no network)
- **common**: errors, utils, configuration

## Layers
- **domain**: entities & enum types
- **application**: use-case services (e.g., `GetEmployeeView`, `AddFeedback`, `CreateAbsence`)
- **web**: controllers + DTOs + mappers (projection/redaction by role)
- **infra**: repositories (JPA), config, seed

## Auth & Authorization (demo)
- Simulated via headers `X-Demo-Role` and `X-Demo-UserId`
- Roles: `MANAGER`, `OWNER`, `COWORKER`
- Access rules:
  - MANAGER/OWNER -> full employee fields; can update
  - COWORKER -> redacted employee fields; can create feedback

## Data Redaction
Sensitive fields (e.g., `salary`, `dob`) are omitted for coworkers via DTO projection in the mapper layer.

## AI Usage
### Purpose
Polish free-text feedback before persisting. This is optional; the app functions without external AI.

### Design
- Application code depends on the port TextPolisher.
- At runtime, a Spring bean chooses:
  - OpenAIPolisher if **AI_API_KEY** is present
  -LocalPolisher otherwise (keeps the app fully runnable)

### Configuration (Spring application.yml)
```bash
app:
  ai:
    baseUrl: ${AI_BASE_URL:https://api.openai.com/v1}
    apiKey:  ${AI_API_KEY:}                 # empty => LocalPolisher
    chatModel: ${AI_CHAT_MODEL:gpt-4o-mini}
    timeoutSeconds: ${AI_TIMEOUT:30}
```

### Environment variables
- AI_BASE_URL – default https://api.openai.com/v1
- AI_API_KEY – OpenAI API key (if unset → fallback)
- AI_CHAT_MODEL – e.g., gpt-4o-mini
- AI_TIMEOUT – request timeout in seconds (default 30)

### Security
- The key is never exposed to the frontend; calls happen server-side.
- .env files are ignored by git; provide backend/.env.example only.

### Error handling & resilience
- Network errors or non-200 responses fall back to LocalPolisher.
- Logs include a short reason tag ([AI]) without leaking secrets.
- Simple rate-limit backoff can be added if required.

## Integration
- REST, versioned at `/api/v1` 
- CORS enabled for `http://localhost:5173` 
- OpenAPI sketch in `docs/openapi.yaml`.

## Extractability (future)
- The ai module can be split into a small service (e.g., /polish) with the same TextPolisher contract.
- Replace H2 with Postgres + Flyway; add OIDC for real auth; add observability.


