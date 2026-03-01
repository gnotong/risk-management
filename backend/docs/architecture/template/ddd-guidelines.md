# DDD & Clean Architecture Guidelines

## 🎯 Objective

Move towards domain-driven design.

---

## Domain Layer

Must contain:
- Entities
- Enums
- Value Objects
- Domain rules

No HTTP or framework dependency.

---

## Application Layer

Must:
- Orchestrate domain logic
- Handle transactions

---

## Infrastructure Layer

Must:
- Handle persistence
- Handle external APIs

---

## Future Direction

Support:
- Event-driven patterns
- Domain events
- Microservices