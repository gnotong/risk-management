Use the global project configuration defined in @backend/   antigravity.json.

Then apply the architectural and refactoring guidelines described in the documentation(*.md files) in @backend/template.

Refactor the backend progressively, module by module.

Ensure:
- No business logic in REST controllers.
- AOP interceptors for cross-cutting concerns.
- Domain exception handling.
- Centralized validation.
- Preservation of business behavior.
- Compatibility with Docker and monorepo.
- Successful build and runtime after each step.

Provide summaries after each module.