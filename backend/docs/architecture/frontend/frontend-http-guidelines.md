# HTTP & API Strategy

## 🎯 Goal

Centralize all backend communication.

---

## Use Axios Wrapper

Create:

infrastructure/http/httpClient.ts

Must include:
- Base URL config
- Request interceptor
- Response interceptor
- Error normalization

---

## Global Error Mapping

Map backend error format:

{
  code,
  message,
  timestamp
}

To frontend error object.

---

## Token Handling

- Read JWT from secure storage
- Attach in Authorization header
- Handle 401 globally