# State Management Guidelines

## 🎯 Goal

Make state predictable and scalable.

---

## Store Rules

- Stores must call use cases
- Stores must not call API directly
- Stores must not contain domain logic

---

## UI State

Separate:
- Global state
- Feature state
- Local component state

---

## Avoid

- Monolithic stores
- Cross-feature coupling