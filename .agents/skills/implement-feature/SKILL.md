---
name: implement-feature
description: Guide an approved CareerPilot AI feature from requirements and architecture analysis through focused implementation, tests, validation, and handoff. Use for feature implementation work.
---

# Implement feature

## 1. Establish scope

- Read applicable repository instructions, including [AGENTS.md](../../../AGENTS.md), [ARCHITECTURE.md](../../../ARCHITECTURE.md), and [the development guide](../../../docs/DEVELOPMENT.md).
- Inspect affected code, API contracts, and nearby tests before proposing changes.
- Identify acceptance criteria, constraints, and unknowns. Clarify unknowns that block safe implementation.
- Inspect the current branch, working tree, and staging area before editing.

## 2. Present the implementation approach

- Explain intended changes, meaningful tests, and significant trade-offs.
- Obtain human approval before implementation if it has not already been authorized. Carry forward existing authorization; do not request the same approval repeatedly.

## 3. Implement within scope

- Follow existing architecture and conventions. Prefer small, focused changes and reuse established patterns.
- Preserve unrelated behavior and user modifications.
- Request approval before significant scope expansion and follow all approval requirements in AGENTS.md.

## 4. Test and validate

- Add or update meaningful tests for new behavior and bug fixes.
- Use focused checks during development and `npm run validate` for comprehensive verification according to AGENTS.md. Follow the development guide for prerequisites and detailed commands.
- Keep quality checks enabled. Report failures and unavailable checks accurately, including their causes and validation limitations.

## 5. Review and hand off

- Review the final diff for correctness, scope, and unintended changes.
- Update architecture documentation when required by AGENTS.md.
- Report implemented changes, test and validation results, remaining limitations, and Git status.

## 6. Respect Git authorization

- Skill invocation does not authorize branch changes, staging, commits, pushes, merges, or pull request operations.
- Require task-specific human authorization for Git mutations. Preserve user changes and do not infer Git authorization from approval to implement a feature.
