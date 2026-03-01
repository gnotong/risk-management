# Global Error Handling Strategy

## 🎯 Objective

Centralize all error handling.

---

## ❌ Forbidden

throw new WebApplicationException(...)

---

## ✅ Allowed

Use domain exceptions:

- BusinessException
- NotFoundException
- ForbiddenException
- UnauthorizedException
- ConflictException

All extend RuntimeException.

---

## 🌍 Global ExceptionMapper

Must convert domain exceptions to HTTP responses.

Error response format:

{
  "code": "ERROR_CODE",
  "message": "Readable message",
  "timestamp": "ISO_DATE"
}

---

## 📌 Mapping Rules

BusinessException → 400
NotFoundException → 404
ForbiddenException → 403
UnauthorizedException → 401
ConflictException → 409