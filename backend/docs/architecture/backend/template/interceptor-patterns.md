# Interceptor Patterns (AOP Strategy)

## 🎯 Purpose

Define all cross-cutting concerns that must be handled via CDI Interceptors.

---

## 🔹 Validation Interceptors

Use custom annotations:

- @ValidateDates
- @ValidateBusinessRules

These must:
- Validate complex domain rules
- Throw domain exceptions
- Not depend on REST

---

## 🔹 Security Interceptors

Use:

- @CheckAccess
- @CheckAvancementAccess

These must:
- Validate authorization
- Check role
- Check entity ownership

No manual role checking in REST.

---

## 🔹 Audit Interceptor

Use:

- @Audit

Must:
- Automatically log changes
- Track modifications
- Persist audit entries

---

## 🔹 Logging Interceptor

Use:

- @LogExecution

Must:
- Log method entry/exit
- Log execution time

---

## 🧩 Interceptor Rules

Each interceptor must:
- Use @InterceptorBinding
- Be reusable
- Be independent
- Be testable