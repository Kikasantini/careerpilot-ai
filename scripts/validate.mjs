import { spawnSync } from "node:child_process";
import { existsSync, realpathSync } from "node:fs";
import { dirname, resolve } from "node:path";

const rootDir = resolve(import.meta.dirname, "..");
const frontendDir = resolve(rootDir, "frontend");
const backendDir = resolve(rootDir, "backend");
const nodeDir = dirname(realpathSync(process.execPath));
const npmCli = [
  process.env.npm_execpath,
  resolve(nodeDir, "node_modules/npm/bin/npm-cli.js"),
  resolve(nodeDir, "../lib/node_modules/npm/bin/npm-cli.js"),
  resolve(nodeDir, "../share/nodejs/npm/bin/npm-cli.js"),
].find((path) => path && existsSync(path));
const prettierCli = resolve(
  frontendDir,
  "node_modules/prettier/bin/prettier.cjs",
);

if (!npmCli || !existsSync(prettierCli)) {
  console.error(
    "Validation requires npm and the installed frontend dependencies. See docs/DEVELOPMENT.md for setup.",
  );
  process.exit(1);
}

// Mirror the shell wrapper's cache lookup before invoking it: -o alone
// prevents Maven artifact downloads but does not prevent wrapper bootstrap.
const offlineMaven = `
set -eu
distributionUrl=
while IFS='=' read -r key value; do
  case "$key" in
    distributionUrl) distributionUrl=$(printf '%s' "$value" | tr -d '[:space:]') ;;
  esac
done < .mvn/wrapper/maven-wrapper.properties
case "$distributionUrl" in
  */apache-maven-*-bin.zip) ;;
  *) echo "Unsupported Maven distribution; refusing automatic bootstrap." >&2; exit 1 ;;
esac
if [ -n "\${MVNW_REPOURL:-}" ]; then
  distributionUrl="$MVNW_REPOURL/org/apache/maven/\${distributionUrl#*/org/apache/maven/}"
fi
archive=\${distributionUrl##*/}
distribution=\${archive%-bin.zip}
remaining=$distributionUrl
hash=0
while [ -n "$remaining" ]; do
  character=\${remaining%"\${remaining#?}"}
  hash=$(( (hash * 31 + $(LC_CTYPE=C printf %d "'$character")) % 4294967296 ))
  remaining=\${remaining#?}
done
cache="\${MAVEN_USER_HOME:-$HOME/.m2}/wrapper/dists/$distribution/$(printf '%x' "$hash")"
if [ ! -x "$cache/bin/mvn" ]; then
  echo "Cached Maven distribution missing or unusable: $cache" >&2
  echo "Provision Maven and its dependencies before validation; see docs/DEVELOPMENT.md." >&2
  exit 1
fi
exec bash ./mvnw -o spotless:check package
`;

const steps = [
  {
    label: "Frontend lint",
    command: process.execPath,
    args: [npmCli, "run", "lint"],
    cwd: frontendDir,
  },
  {
    label: "Frontend format check",
    command: process.execPath,
    args: [prettierCli, "--check", "."],
    cwd: frontendDir,
  },
  {
    label: "Frontend production build",
    command: process.execPath,
    args: [npmCli, "run", "build"],
    cwd: frontendDir,
  },
  {
    label: "Backend offline format check, build, and tests",
    command: "bash",
    args: ["-c", offlineMaven],
    cwd: backendDir,
  },
  {
    label: "Python validation",
    command: process.execPath,
    args: [resolve(rootDir, "scripts/check-python.mjs"), "--all"],
    cwd: rootDir,
  },
];

for (const { label, command, args, cwd } of steps) {
  console.log(`\nValidation: ${label}`);
  const result = spawnSync(command, args, {
    cwd,
    stdio: "inherit",
    shell: false,
  });

  if (result.error || result.status !== 0) {
    console.error(
      `${label} failed: ${result.error?.message ?? result.signal ?? `exit ${result.status}`}`,
    );
    console.error(
      "Check the output above and docs/DEVELOPMENT.md. Git Bash, JDK 21, cached Maven artifacts, and the project Python environment are required.",
    );
    process.exit(result.status || 1);
  }
}

console.log("\nAll project validation checks passed.");
