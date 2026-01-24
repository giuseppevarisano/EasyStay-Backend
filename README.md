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

Come testare il progetto in locale

Se vuoi provare l'applicazione senza configurare un database MySQL:

    Apri il progetto in IntelliJ.

    Vai in Edit Configurations.

    Nel campo Active Profiles, scrivi: h2.

    Avvia l'applicazione.

    Accedi a Swagger su http://localhost:8080/swagger-ui/index.html o alla console H2 su http://localhost:8080/h2-console

##  Come avviare l'applicazione (Docker)

Assicurati di avere Docker installato e attivo, quindi esegui:

1. **Build del JAR**: `mvn clean package -DskipTests`
2. **Build dell'immagine**: `docker build -t easystay-backend .`
3. **Run del container**: `docker run -p 8081:8080 --name easystay-app easystay-backend`

## Database & Documentazione
L'applicazione utilizza un database in memoria **H2** popolato con 50.000 record di test.

- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **H2 Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
    - **JDBC URL**: `jdbc:h2:mem:easystaydb`
    - **User**: `sa`
    - **Password**: *(vuota)*