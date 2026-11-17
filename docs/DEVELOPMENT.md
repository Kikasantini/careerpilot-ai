# Local development

Windows with Git Bash and VS Code is the primary development environment. The commands below use Git Bash. See [AGENTS.md](../AGENTS.md) for repository rules and [ARCHITECTURE.md](../ARCHITECTURE.md) for module boundaries and API flows.

## Prerequisites and setup

- Install Git for Windows with Git Bash. The pre-commit hook uses a shell, and its Java formatter invokes `bash`.
- Use Node.js and npm for both the root tooling and the frontend. The repository pins package versions through the [root lockfile](../package-lock.json) and [frontend lockfile](../frontend/package-lock.json). Node.js 22.22.1 or a later 22.x release, or Node.js 24.x, satisfies the recorded engine ranges for the locked Vite, ESLint, and lint-staged versions. These choices are based on dependency metadata and have yet to be validated through a clean installation. The repository has no Node.js version pin.
- Use JDK 21, as configured in [backend/pom.xml](../backend/pom.xml). The [Maven Wrapper](../backend/.mvn/wrapper/maven-wrapper.properties) selects Maven 3.10.0 and downloads it on first use if needed. Maven resolves the backend dependencies when you run a goal.
- Use Python 3.12 and a local virtual environment. The Python version is specified in [AGENTS.md](../AGENTS.md) and reflected in the Ruff target in [ai-service/pyproject.toml](../ai-service/pyproject.toml).

From the repository root, install the two npm packages from their lockfiles. The root package's `prepare` script configures Husky. See the [root package manifest](../package.json) and [frontend package manifest](../frontend/package.json).

```bash
npm ci
npm --prefix frontend ci
git config --get core.hooksPath
```

The expected hooks path is `.husky/_`. If it is missing, run `npm run prepare` from the repository root and check again.

From a separate Git Bash session at the repository root, create and activate the Python environment and install the versions listed in [requirements.txt](../ai-service/requirements.txt).

```bash
cd ai-service
py -3.12 -m venv .venv
source .venv/Scripts/activate
python -m pip install -r requirements.txt
```

The example assumes the Windows `py` launcher is available. If `python --version` already reports Python 3.12, `python -m venv .venv` works as the creation command. In later Git Bash sessions, activate the existing environment from `ai-service/`.

## Environment diagnostics

Run each row from the indicated directory. Commands for Git, Node.js, npm, and Java require no virtual environment.

| Directory | Version commands |
| --- | --- |
| Repository root | `git --version`, `node --version`, `npm --version`, `java -version` |
| `backend/` | `bash ./mvnw --version` (the wrapper may download Maven on first use) |
| `ai-service/` | Activate with `source .venv/Scripts/activate`, then run `python --version`, `python -m pip --version`, `python -m ruff --version` if Ruff is available, and `python -m pytest --version` after installing the listed requirements. |

## Start the modules

Run each row in a separate terminal from the indicated directory.

| Directory | Command | Where to look |
| --- | --- | --- |
| `ai-service/` | `python -m uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload` | Activate `.venv` first. This invocation uses Uvicorn from `requirements.txt` and the app object in [app/main.py](../ai-service/app/main.py). |
| `backend/` | `./mvnw spring-boot:run` | Uses the Spring Boot plugin in [pom.xml](../backend/pom.xml). The local frontend proxy expects port `8080`, and [application.yaml](../backend/src/main/resources/application.yaml) points the health client to port `8000`. |
| `frontend/` | `npm run dev` | Open the URL printed by Vite. See [package.json](../frontend/package.json) and [vite.config.ts](../frontend/vite.config.ts). |

To check the running services, use `curl -f http://localhost:8080/api/jobs/hello` for Spring and `curl -f http://localhost:8080/api/ai/health` for the Spring-to-Python connection.

## Coding and test conventions

Follow the existing naming and formatting patterns in the nearest source and tests. Use synthetic test data and write comments and documentation in English. See [AGENTS.md](../AGENTS.md) for change requirements.

- **Java.** Source packages sit under `ai.careerpilot.api`. Classes and records use PascalCase, methods and variables use lower camel case, and types use role suffixes such as `Controller` and `Service` where applicable. Spotless uses Google Java Format. JUnit tests mirror source packages under `backend/src/test/java` and use descriptive scenario names such as `shouldPrioritizeDescriptionWhenBothAreProvided`. Existing tests use direct service calls, MockMvc for job routes, and `MockRestServiceServer` for outbound HTTP. See [job service tests](../backend/src/test/java/ai/careerpilot/api/service/JobAnalysisServiceTest.java), [controller tests](../backend/src/test/java/ai/careerpilot/api/controller/JobControllerTest.java), and [HTTP client tests](../backend/src/test/java/ai/careerpilot/api/client/AiServiceClientTest.java).
- **TypeScript.** React components use PascalCase in `.tsx` files. Functions, variables, and service filenames use lower camel case. Prettier configures two-space indentation, single quotes, semicolons, trailing commas, and a print width of 100. ESLint includes TypeScript, React Hooks, and React Refresh rules. See [App.tsx](../frontend/src/App.tsx), [jobService.ts](../frontend/src/services/jobService.ts), [Prettier config](../frontend/.prettierrc.json), and [ESLint config](../frontend/eslint.config.js).
- **Python.** Existing modules and functions use snake_case. Ruff configures a line length of 100, double quotes, space indentation, and import sorting. Pytest uses `test_*.py` files and `test_*` functions. The health test uses FastAPI's `TestClient` and asserts both status and JSON body without starting Uvicorn. See [app/main.py](../ai-service/app/main.py), [test_main.py](../ai-service/tests/test_main.py), and [pyproject.toml](../ai-service/pyproject.toml).

## Build, lint, format, and test

Run checks from the indicated directory. Activate `.venv` before running the Python commands.

| Directory | Build | Lint and format checks | Tests |
| --- | --- | --- | --- |
| `frontend/` | `npm run build` | `npm run lint` and `npm exec -- prettier --check .` | No test script configured |
| `backend/` | `./mvnw package` | `./mvnw spotless:check` | `./mvnw test` |
| `ai-service/` | No package build configured | `python -m ruff check .` and `python -m ruff format --check .` when Ruff is available in `.venv` | `python -m pytest` |

To apply formatting, use `npm exec -- prettier --write <paths>` from `frontend/`, `./mvnw spotless:apply` from `backend/`, or `python -m ruff format <paths>` from `ai-service/` if Ruff is available. Review the resulting diff for scope.

## Git hooks and automated checks

The [Husky pre-commit hook](../.husky/pre-commit) runs `npx lint-staged`, the frontend build, and backend tests in that order. It stops at the first failure.

The [lint-staged configuration](../lint-staged.config.mjs) runs ESLint fixes and Prettier on matching frontend source files, Prettier on other matching frontend and tooling files, and [a Spotless wrapper](../scripts/format-staged-java.mjs) on staged backend Java files. The frontend build and backend tests run even when no staged files match a lint-staged rule.

## Known tooling gaps

- The [frontend package](../frontend/package.json) runs `husky` in its `prepare` script and leaves Husky undeclared in its own dependencies. The [root package](../package.json) declares it. A standalone frontend installation may fail during `prepare` if Husky is unavailable on the script's PATH.
- Git tracks [backend/mvnw](../backend/mvnw) with mode `100644`, without the executable bit. Direct execution with `./mvnw` can fail on Unix checkouts, including future Unix CI runners.
- Ruff has configuration but is absent from `requirements.txt`. The standard Python setup does not make the Ruff commands available by itself.
- The root `npm test` is a failing placeholder. The frontend has no test script, and the repository has no checked-in end-to-end suite or CI workflow.
- Pre-commit has no Python lint, format, or test step. Root and `docs/` Markdown fall outside the current lint-staged rules.
