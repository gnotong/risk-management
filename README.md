# Documentation

run the project 

start docker in windows

go to the root of the project in wsl

```bash
docker compose up -d
```

if you want to reset all

```bash
docker compose down -v && docker compose up -d --build
```

then access to the project at 

Keycloak: http://localhost:8080

Frontend: http://localhost