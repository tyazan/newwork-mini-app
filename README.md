# NEWWORK Full‑Stack Mini‑App (AI‑Forward)

Single‑page HR profile slice with role‑based visibility, feedback (optional AI polish), and absence request.
Designed as a **modular monolith** so it’s easy to integrate now and split later if needed.

**Tech & Run targets**
- Backend: Java 21 + Spring Boot (Web, Data JPA, H2). Build with Maven 3.9+.
- Frontend: React + Vite + TypeScript.
.
├─ backend/      # Spring Boot API (H2 seed data)
├─ frontend/     # Vite/React SPA
└─ docs/         # (optional) openapi, screenshots, notes

## Quickstart

### Backend
```bash
cd backend
mvn -q spring-boot:run
```
The backend starts on `http://localhost:8080` with an in‑memory H2 database and seed data.

### Frontend
```bash
cd frontend
npm install
npm run dev
```
The SPA serves on `http://localhost:5173` and calls the backend at `http://localhost:8080`.

**Notes:** AI polish is optional. If no key is set, the app uses a LocalPolisher fallback.

## Enable OpenAI polish (optional):

### PowerShell (Windows):
```bash
$env:AI_BASE_URL = "https://api.openai.com/v1"
$env:AI_API_KEY  = "<your-openai-key>"
$env:AI_CHAT_MODEL = "gpt-4o-mini"
$env:AI_TIMEOUT = "30"
mvn -q spring-boot:run
```

### CMD (Windows):
```bash
set AI_BASE_URL=https://api.openai.com/v1
set AI_API_KEY=<your-openai-key>
set AI_CHAT_MODEL=gpt-4o-mini
set AI_TIMEOUT=30
mvn -q spring-boot:run
```

### macOS/Linux:
```bash
export AI_BASE_URL=https://api.openai.com/v1
export AI_API_KEY=<your-openai-key>
export AI_CHAT_MODEL=gpt-4o-mini
export AI_TIMEOUT=30
mvn -q spring-boot:run
```

### Demo users / roles (simulated)
Use the **role switcher** in the top-right of the SPA or send headers:
- `X-Demo-Role`: `MANAGER` | `OWNER` | `COWORKER`
- `X-Demo-UserId`: requester numeric id (e.g. `2`)

### Roles & permissions
- **MANAGER**: see & edit **any** employee; can create absence for anyone (demo).
- **OWNER**: see & edit **only their own** record (`X-Demo-UserId == {id}`).
- **COWORKER**: read-only redacted view (no salary/DOB); can post feedback.

### Endpoints (subset)
- `GET /api/v1/employees/{id}` → returns full or redacted view by role & identity
- `PUT /api/v1/employees/{id}` → update employee (allowed: MANAGER; OWNER only for their own id)
- `GET /api/v1/employees/{id}/feedback`
- `POST /api/v1/employees/{id}/feedback` → `{ text, polish: boolean }`
- `POST /api/v1/employees/{id}/absences`

### AI polish
The backend uses a pluggable **TextPolisher**:
- **OpenAIPolisher** (server-side) if `AI_API_KEY` is set
- **LocalPolisher** fallback (no network) if not set

The app is fully runnable without external AI.

### Architecture (short)
[React/Vite SPA] --CORS--> [Spring Boot API] --(optional)--> [OpenAI Chat Completions]
                               |
                             [H2 DB]

- **Style:** Modular monolith (Spring Boot backend + React SPA)
- **Modules:** `hrprofile`, `feedback`, `absence`, `ai`, `common`
- **Layers:** entities · repositories · services · controllers (DTOs/mappers)
- **AuthZ:** headers simulate roles; redaction via DTO/projections
- **API style:** versioned REST (`/api/v1`)
- **AI:** interface-based TextPolisher with OpenAIPolisher (remote) and LocalPolisher (fallback)

See more in `docs/ARCHITECTURE.md` and `docs/openapi.yaml`.

### With‑more‑time
- Real auth (OIDC), Postgres + Flyway, approvals & audit trail, observability, pagination.

---
**Notes:** This is a runnable slice intended to demonstrate structure, cleanliness, and pragmatic AI integration (with graceful degradation).
