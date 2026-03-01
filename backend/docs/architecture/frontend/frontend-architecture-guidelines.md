# Frontend Architecture Guidelines (Vue 3 + TypeScript)

## 🎯 Objective

Build a scalable, testable, and maintainable frontend
using Clean Architecture principles.

---

## 🧱 Architecture Layers

Presentation → Application → Domain → Infrastructure

Each layer must be independent.

---

## 📦 Domain Layer

Contains:
- Entities
- Value objects
- Domain rules
- Interfaces for repositories

Must NOT:
- Call HTTP
- Use Vue
- Depend on Pinia
- Depend on Axios

Pure TypeScript only.

---

## 📦 Application Layer

Contains:
- Use cases
- DTO transformation
- Business orchestration

Example:
- createRiskUseCase
- updatePlanActionUseCase
- closeRiskUseCase

Must call domain and repositories.

---

## 📦 Infrastructure Layer

Contains:
- API clients (Axios)
- HTTP interceptors
- Mappers
- Repository implementations

Must implement domain repository interfaces.

---

## 📦 Presentation Layer

Contains:
- Vue components
- Views
- Composables
- UI state logic

Must call use cases.
Must not call API directly.

---

## 🧠 State Management (Pinia)

Rules:
- Stores must call use cases
- No API calls inside stores
- No business logic inside components

---

## 🔐 Authentication

- Centralized token management
- Axios interceptor for JWT
- Global error interceptor

---

## ⚠️ Error Handling

- Centralized HTTP error handler
- Map backend domain errors to UI messages
- No raw Axios errors in components

---

## 🚨 Forbidden

- API calls inside Vue components
- Business logic inside components
- Direct Axios calls outside infrastructure
- Massive stores mixing everything

---

## 🧪 Testing

- Unit tests for use cases
- Unit tests for domain
- Component tests
- Mock infrastructure layer