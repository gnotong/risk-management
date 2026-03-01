# Security Architecture

## 🎯 Goal

Centralize access control.

---

## Role-based security

Use:
- @RolesAllowed
- CDI interceptors for advanced rules

---

## Forbidden

- Manual role checking in REST controllers
- Header parsing logic inside REST

---

## Advanced Access Rules

Complex ownership rules must be implemented
inside interceptors.