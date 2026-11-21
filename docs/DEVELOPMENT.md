# Local development

Windows with Git Bash and VS Code is the primary development environment. The commands below use Git Bash. See [AGENTS.md](../AGENTS.md) for repository rules and [ARCHITECTURE.md](../ARCHITECTURE.md) for module boundaries and API flows.

## Prerequisites and setup

- Install Git for Windows with Git Bash. The pre-commit hook uses a shell, and its Java formatter invokes `bash`.
- Use Node.js 26 for both the root tooling and the frontend. The root [.nvmrc](../.nvmrc) contains the baseline `26.13.0`. Both npm manifests declare `engines.node` as `>=26 <27` and `engines.npm` as `>=11 <12`. These declarations do not automatically install or enforce versions. The repository pins package versions through the [root lockfile](../package-lock.json) and [frontend lockfile](../frontend/package-lock.json).
- Use JDK 21, as configured in [backend/pom.xml](../backend/pom.xml). The [Maven Wrapper configuration](../backend/.mvn/wrapper/maven-wrapper.properties) specifies Wrapper version 3.3.4 and Maven distribution version 3.10.0. The wrapper downloads that distribution on first use if needed. Maven resolves the backend dependencies when you run a goal. Git tracks [backend/mvnw](../backend/mvnw) as executable with mode `100755`.
- Python 3.12 is the supported development baseline. Use a local virtual environment. The version is specified in [AGENTS.md](../AGENTS.md), declared in [ai-service/.python-version](../ai-service/.python-version), and reflected in the Ruff target in [ai-service/pyproject.toml](../ai-service/pyproject.toml). Compatible version managers can read `.python-version`; the file itself does not automatically install or enforce Python.

From the repository root, install the two npm packages from their lockfiles. The root package owns the Husky dependency and configures hooks through its `prepare` script. See the [root package manifest](../package.json) and [frontend package manifest](../frontend/package.json).

```bash
npm ci
npm --prefix frontend ci
git config --get core.hooksPath
```

The expected hooks path is `.husky/_`. If it is missing, run `npm run prepare` from the repository root and check again.

From a separate Git Bash session at the repository root, create and activate the Python environment and install the developer requirements.

```bash
cd ai-service
py -3.12 -m venv .venv
source .venv/Scripts/activate
python -m pip install -r requirements-dev.txt
```

The example assumes the Windows `py` launcher is available. If `python --version` already reports Python 3.12, `python -m venv .venv` works as the creation command. In later Git Bash sessions, activate the existing environment from `ai-service/`.

Windows developers can continue using `py -3.12`. On Linux/macOS, select Python 3.12 with your available interpreter or version manager. From the repository root, use the following commands when `python3.12` is available.

```bash
cd ai-service
python3.12 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements-dev.txt
```

For subsequent sessions and the validation commands below, Linux/macOS developers activate the environment with `source .venv/bin/activate` from `ai-service/`.

[requirements.txt](../ai-service/requirements.txt) pins runtime dependencies, their transitive dependencies, and the existing optional Uvicorn packages. [requirements-dev.txt](../ai-service/requirements-dev.txt) includes that file and adds pinned test dependencies and Ruff. For runtime-only setup, use `python -m pip install -r requirements.txt` from `ai-service/` with the virtual environment activated.

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

For comprehensive project validation, run from the repository root.

```bash
npm run validate
```

The [validation orchestrator](../scripts/validate.mjs) runs the following checks sequentially, stopping at the first failure with a nonzero exit code.

1. Frontend ESLint through `npm run lint`.
2. Frontend Prettier `--check .` using the installed frontend tool.
3. Frontend production build through `npm run build`.
4. Backend Maven Wrapper `-o spotless:check package`, including formatting checks, compilation, tests, JAR creation, and Spring Boot repackaging.
5. The [Python helper](../scripts/check-python.mjs) with `--all`, running Ruff lint, Ruff format check, and pytest once each.

Validation runs independently of staged files and uses the existing installed dependencies. It does not install dependencies automatically. Bash, JDK 21, the frontend npm dependencies, and the project Python 3.12 environment with developer requirements must be available. The shell wrapper's selected Maven distribution and all required dependency and plugin artifacts must already be cached locally. A missing wrapper distribution stops validation before bootstrap; missing Maven artifacts fail in offline mode. Provision missing artifacts as a separate setup operation before retrying.

Formatting is checked without applying fixes. Builds generate their normal output artifacts. Unlike pre-commit, this command checks all modules unconditionally and does not invoke lint-staged or its automatic formatting tasks.

`npm run validate` passed on Windows with Git Bash outside the sandbox, including 25 Java tests and one Python test. Windows execution needs permissions allowing Python's socket-pair communication. Linux/macOS execution remains unvalidated.

Run checks from the indicated directory. Activate `.venv` before running the Python commands.

| Directory | Build | Lint and format checks | Tests |
| --- | --- | --- | --- |
| `frontend/` | `npm run build` | `npm run lint` and `npm exec -- prettier --check .` | No test script configured |
| `backend/` | `./mvnw package` | `./mvnw spotless:check` | `./mvnw test` |
| `ai-service/` | No package build configured | `python -m ruff check .` and `python -m ruff format --check .` when Ruff is available in `.venv` | `python -m pytest` |

To apply formatting, use `npm exec -- prettier --write <paths>` from `frontend/`, `./mvnw spotless:apply` from `backend/`, or `python -m ruff format <paths>` from `ai-service/` if Ruff is available. Review the resulting diff for scope.

## Git hooks and automated checks

The [Husky pre-commit hook](../.husky/pre-commit) runs `npx lint-staged`, conditional Python validation, the frontend production build, and backend tests in that order. It stops at the first failure.

The [lint-staged configuration](../lint-staged.config.mjs) runs ESLint fixes and Prettier on matching frontend source files, Prettier on other matching frontend and tooling files, and [a Spotless wrapper](../scripts/format-staged-java.mjs) on staged backend Java files. The frontend build and backend tests run even when no staged files match a lint-staged rule.

The [Python validation helper](../scripts/check-python.mjs) runs when staged changes affect any path under `ai-service/`, `scripts/check-python.mjs`, or `.husky/pre-commit`. Additions, modifications, deletions, and both paths of renames participate. Otherwise, it prints a skip message and succeeds without requiring Python.

When triggered, the helper uses `ai-service/.venv/Scripts/python.exe` on Windows or `ai-service/.venv/bin/python` on Linux/macOS. It validates Python 3.12 and the availability of Ruff and pytest, then runs `python -m ruff check .`, `python -m ruff format --check .`, and `python -m pytest` once each, in that order, from `ai-service/`. A missing environment or tool causes a clear failure with setup guidance. The helper never installs dependencies automatically.

Python checks inspect working-tree files, including unstaged changes. Validation covers the working tree rather than an isolated snapshot of staged content.

## Known tooling gaps

- Clean installations and Linux/macOS execution have yet to be validated. The declared Node.js and npm ranges express intended support; compatibility across every version in those ranges has yet to be tested.
- The root `npm test` is a failing placeholder. The frontend has no test script, and the repository has no checked-in end-to-end suite or CI workflow.
- The complete pre-commit workflow passed on Windows with Git Bash outside the sandbox. Linux/macOS execution, failure paths, and staged Java formatting remain unvalidated.
- Root and `docs/` Markdown fall outside the current lint-staged rules.
