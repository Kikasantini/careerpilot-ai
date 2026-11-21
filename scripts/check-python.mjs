import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import { resolve } from "node:path";

const rootDir = resolve(import.meta.dirname, "..");
const serviceDir = resolve(rootDir, "ai-service");

const args = process.argv.slice(2);

if (args.length > 1 || (args.length === 1 && args[0] !== "--all")) {
  console.error(
    "Invalid arguments. Usage: node scripts/check-python.mjs [--all]",
  );
  process.exit(1);
}

if (args.length === 0) {
  // Treat renames as deletion/addition pairs so both paths participate.
  const staged = spawnSync(
    "git",
    ["diff", "--cached", "--name-only", "--no-renames", "-z", "--"],
    { cwd: rootDir, encoding: "utf8", shell: false },
  );

  if (staged.stderr) {
    process.stderr.write(staged.stderr);
  }

  if (staged.error || staged.status !== 0) {
    console.error(
      "Unable to inspect staged changes.",
      staged.error?.message ?? "",
    );
    process.exit(staged.status || 1);
  }

  const relevant = staged.stdout
    .split("\0")
    .some(
      (path) =>
        path.startsWith("ai-service/") ||
        path === "scripts/check-python.mjs" ||
        path === ".husky/pre-commit",
    );

  if (!relevant) {
    console.log("Skipping Python checks: no relevant changes are staged.");
    process.exit(0);
  }
}

const python = resolve(
  serviceDir,
  process.platform === "win32"
    ? ".venv/Scripts/python.exe"
    : ".venv/bin/python",
);
const setupHelp =
  "See docs/DEVELOPMENT.md to create the Python 3.12 environment and install ai-service/requirements-dev.txt.";

if (!existsSync(python)) {
  console.error(
    `Project Python interpreter is missing: ${python}\n${setupHelp}`,
  );
  process.exit(1);
}

const preflight = [
  "import importlib.util, sys",
  "if sys.version_info[:2] != (3, 12):",
  "    sys.exit('Python 3.12 is required; found ' + sys.version.split()[0])",
  "missing = [name for name in ('ruff', 'pytest') if importlib.util.find_spec(name) is None]",
  "if missing:",
  "    sys.exit('Missing Python tools: ' + ', '.join(missing))",
].join("\n");

const commands = [
  { label: "Validate Python environment", args: ["-c", preflight] },
  { label: "Ruff lint", args: ["-m", "ruff", "check", "."] },
  {
    label: "Ruff format check",
    args: ["-m", "ruff", "format", "--check", "."],
  },
  { label: "pytest", args: ["-m", "pytest"] },
];

for (const { label, args } of commands) {
  console.log(`Python checks: ${label}`);
  const result = spawnSync(python, args, {
    cwd: serviceDir,
    stdio: "inherit",
    shell: false,
  });

  if (result.error || result.status !== 0) {
    console.error(
      `${label} failed: ${result.error?.message ?? result.signal ?? `exit ${result.status}`}`,
    );
    console.error(setupHelp);
    process.exit(result.status || 1);
  }
}
