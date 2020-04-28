# Universal Quarkus Todo Application Spring

 This project uses Spring Boot.
It:

* has a REST API via Spring Web MVC
* uses Spring Data a PostgreSQL database
* uses OpenId Connect and JWT token to secure the application via Spring Security Oauth2 Client
* uses Qute as template engine 

The frontend uses Vue.js.

## Prerequisites

The application uses PostgreSQL and Keycloak.
Refer to the provide `docker-compose` infrastructure to start the required services.
Run: 

## Running the application in dev mode

You can run your application using:
```
./mvnw spring-boot:run
```

Open http://localhost:8080 and log in with one of the user mentioned above.

## Packaging and running the application

The application is packaged using `./mvnw package`.
It produces the executable jar  `universal-todo-1.0.0-SNAPSHOT.jar` file in `/target` directory.

The application is now runnable using `java -jar target/universal-todo-1.0.0-SNAPSHOT.jar`.