# Universal Quarkus Todo Application Quarkus

This project aims to explain a smooth migration path from a Spring Boot application to Quarkus.
It:

## Infrastructure service

Regardless of the runtime, the application uses PostgreSQL and Keycloak.
A `infrastructure/docker-compose` file starts these required services.
Run: 

```shell
docker-compose -f ./infratructure/docker-compose.yaml up
```

Once started (can take a few minutes), open http://localhost:8180/auth/admin/ and login with `admin/admin`.