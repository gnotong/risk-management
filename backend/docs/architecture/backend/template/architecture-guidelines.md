# Backend Architecture Guidelines (Quarkus)

## 🎯 Objective

Standardize the backend architecture and enforce clean code principles
across the entire project.

These rules apply to:
- Existing code
- Future development
- Automated refactoring

---

## 🧱 Architecture Style

The project must follow a layered clean architecture:

REST → Application Service → Domain → Repository

Each layer has a single responsibility.

---

## 📦 Package Structure

com.company.project
 ├── rest
 ├── service
 ├── domain
 ├── repository
 ├── interceptor
 ├── validation
 ├── exception
 ├── mapper
 ├── security
 ├── audit

---

## 🚨 REST Layer Rules

Controllers must:
- Only handle HTTP
- Not contain business logic
- Not contain validation logic
- Not contain authorization logic
- Not throw WebApplicationException

Controllers must be thin.

---

## 💼 Service Layer Rules

Services must:
- Contain business logic
- Handle state transitions
- Control transactions
- Be testable

---

## ⚙️ Cross-Cutting Concerns

Use CDI interceptors for:
- Validation
- Access control
- Logging
- Auditing
- Risk lifecycle synchronization
- Status synchronization

No duplication allowed.

---

## ❌ Forbidden

- Business logic in controllers
- Repeated validation
- Direct security checks in REST
- WebApplicationException in business code

---

## 🔄 Refactoring Scope

All REST resources must be refactored
to comply with these rules.