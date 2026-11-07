import { spawnSync } from "node:child_process";
import { isAbsolute, relative, resolve, sep } from "node:path";

const rootDir = resolve(import.meta.dirname, "..");
const backendDir = resolve(rootDir, "backend");

// Escape special regex characters so file paths are matched literally.
function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

// Receive the Java file paths provided by lint-staged.
const files = process.argv.slice(2);

if (files.length === 0) {
  console.log("No Java files to format.");
  process.exit(0);
}

// Convert relative or absolute paths into absolute paths.
const absoluteFiles = files.map((file) => resolve(rootDir, file));
const relativeFiles = absoluteFiles.map((file) => relative(backendDir, file));

// Prevent the script from processing files outside the backend directory.
for (const file of relativeFiles) {
  if (file === ".." || file.startsWith(`..${sep}`) || isAbsolute(file)) {
    throw new Error(`File is outside the backend directory: ${file}`);
  }
}

// Normalize path separators to support both Windows and Unix systems.
const normalizedFiles = relativeFiles.map((file) => file.split(sep).join("/"));

// Build a regex that matches the selected files by path, not just filename.
const filePatterns = normalizedFiles.map((file) =>
  escapeRegex(file).replaceAll("/", "[/\\\\]"),
);

// Spotless matches against full paths, so allow any prefix before the backend-relative path.
const spotlessPattern = `.*(?:${filePatterns.join("|")})$`;

const mavenWrapper = resolve(backendDir, "mvnw");

const mavenArgs = [
  "-f",
  resolve(backendDir, "pom.xml"),
  "spotless:apply",

  // Preserve regex backslashes when arguments pass through the Maven Bash wrapper.
  `-DspotlessFiles=${spotlessPattern.replaceAll("\\", "\\\\")}`,
];

// Run the Maven Bash wrapper without passing the regex through the Windows command shell.
const result = spawnSync("bash", [mavenWrapper, ...mavenArgs], {
  cwd: backendDir,
  stdio: "inherit",
  shell: false,
});

// Propagate Maven execution errors so the Git hook can reject a failed commit.
if (result.error) {
  throw result.error;
}

process.exit(result.status ?? 1);
