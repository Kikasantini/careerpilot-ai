# AGENTS.md

## Purpose and current scope

CareerPilot AI aims to help users tailor résumés and cover letters to job postings using verified professional information.

- `frontend/` owns the React and TypeScript UI. It submits a job URL or pasted description to Spring and displays the response.
- `backend/` owns the Spring Boot API, request validation, and calls to Python. Job intake currently acknowledges the request and identifies the selected input source.
- `ai-service/` owns the FastAPI service. Its only application endpoint is `GET /health`.
- The root npm package, `.husky/`, and `scripts/` provide commit-time tooling.

Document generation and model integration are planned. Persistence technology will be decided separately.

## Development conventions

- Windows with Git Bash and VS Code is the primary local development environment. Other compatible environments are supported. Use Python 3.12 and the project virtual environment. From `ai-service/`, activate it with `source .venv/Scripts/activate` in Git Bash.
- Keep browser API calls in `frontend/src/services/` and use relative `/api` URLs. See `frontend/vite.config.ts` for local routing.
- Keep Spring controllers focused on HTTP and validation, use DTO records for payloads, and put application logic in services. Route Python calls through the backend client layer. The current integration checks service health.
- Preserve existing behavior and API contracts unless the task explicitly requires a change. Apply Clean Code and SOLID principles where they improve clarity. Reuse existing patterns and avoid unnecessary abstractions.
- Add or update tests for new behavior and bug fixes. Use synthetic test data. Write comments and documentation in English.

## Verification

Run commands from the directory shown. Activate the Python virtual environment first. Ruff commands require Ruff to be available in that environment.

| Directory | Build | Lint and format checks | Test |
|---|---|---|---|
| `frontend/` | `npm run build` | `npm run lint`, `npm exec -- prettier --check .` | No test script is configured. |
| `backend/` | `./mvnw package` | `./mvnw spotless:check` | `./mvnw test` |
| `ai-service/` | No package build is configured. | `python -m ruff check .`, `python -m ruff format --check .` | `python -m pytest` |

To apply formatting, use `npm exec -- prettier --write <paths>`, `./mvnw spotless:apply`, or `python -m ruff format <paths>` in the relevant module. Review formatting changes for scope.

Keep Git hooks enabled. The pre-commit hook runs lint-staged, the frontend build, and backend tests. Root `npm test` is a failing placeholder. Run checks relevant to the change and report what passed, what failed, and which relevant checks were skipped or blocked and why. Never weaken tests or bypass quality checks to make validation pass.

## Git, approval, and privacy

- Inspect the current branch and working tree before editing. Work on a task branch off `main`, preserve existing user changes, and review the final diff for scope. Use Conventional Commits and keep each approved commit logically scoped.
- Present a plan before implementing complex tasks. Explain significant technical decisions and trade-offs.
- Prefer free and open-source tools and services.
- Get explicit human approval before expanding the agreed scope, adding dependencies, making architectural changes, using paid services, committing, pushing, or merging.
- Never commit credentials, secrets, tokens, or real personal data. Get explicit approval before sending personal information to an external service.

## Where to look next

Start with the affected module’s manifest, entry point, and nearest tests. Follow more specific repository instructions where present. Keep this file focused on shared rules and entry points, and link to deeper documentation and reusable skills as they are added.
