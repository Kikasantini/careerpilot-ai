export default {
  "frontend/**/*.{js,jsx,ts,tsx}": [
    "npm exec --prefix frontend -- eslint --fix",
    "npm exec --prefix frontend -- prettier --write",
  ],
  "frontend/**/*.{json,css,md,html}": [
    "npm exec --prefix frontend -- prettier --write",
  ],
  "*.{js,mjs,cjs,json}": "npm exec --prefix frontend -- prettier --write",
  "scripts/**/*.{js,mjs,cjs}": "npm exec --prefix frontend -- prettier --write",
  "backend/**/*.java": (files) => [
    `node scripts/format-staged-java.mjs ${files.map((file) => JSON.stringify(file)).join(" ")}`,
  ],
};
