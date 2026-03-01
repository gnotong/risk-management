Use the configuration defined in @backend/cloud/antigravity-cloud.json.

Transform the current monolithic architecture into a cloud-native, hexagonal, and event-driven architecture progressively and apply the architectural and refactoring guidelines described in @backend/template/*.md files.

Apply:
- AOP interceptors
- Domain-driven design
- Event-driven communication
- Global exception handling
- Centralized validation
- Observability and resilience

Use the strangler pattern to migrate module by module.

Preserve business behavior.

Ensure the system builds and runs after each refactoring step.

Provide a roadmap and summary after each module.