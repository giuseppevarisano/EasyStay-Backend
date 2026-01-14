# EasyStay - Backend

Sistema REST per la gestione delle prenotazioni di case vacanza.

## Architettura e Tecnologie
- Java 17 e Spring Boot 3
- Sicurezza: Autenticazione basata su JWT con Spring Security
- Persistenza: Spring Data JPA (Hibernate) su database H2
- API Documentation: OpenAPI 3 (Swagger)

## Testing e Qualità
- Unit Testing: JUnit 5 e Mockito per l'isolamento della logica di business.
- Integration Testing: MockMvc per la verifica degli endpoint e del ciclo di vita delle risorse.
- Analisi Coverage: Jacoco.

Per eseguire la build e i test:
mvn clean verify