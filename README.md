# EasyStay - Backend

![CI/CD Pipeline](https://github.com/giuseppevarisano/EasyStay-Backend/actions/workflows/deploy.yml/badge.svg)

Sistema REST per la gestione delle prenotazioni di case vacanza.

## 🛠 Architettura e Tecnologie
- **Java 17** e **Spring Boot 3**
- **Sicurezza**: Autenticazione basata su JWT con Spring Security
- **Persistenza**: Spring Data JPA (Hibernate) su database H2
- **API Documentation**: OpenAPI 3 (Swagger)
- **CI/CD**: GitHub Actions con pubblicazione automatica su GHCR

## 🧪 Testing e Qualità
- **Unit Testing**: JUnit 5 e Mockito per l'isolamento della logica di business.
- **Integration Testing**: MockMvc per la verifica degli endpoint e del ciclo di vita delle risorse.
- **Analisi Coverage**: Jacoco.

Per eseguire la build e i test manualmente:
`mvn clean verify`

---

## 🚀 Come avviare l'applicazione

### Opzione A: Tramite Docker (Consigliato)
L'applicazione è disponibile come immagine pre-configurata sul **GitHub Container Registry**. Puoi avviarla senza compilare nulla:

1. **Scarica l'immagine**:
   `docker pull ghcr.io/giuseppevarisano/easystay-backend:latest`
2. **Avvia il container**:
   `docker run -p 8081:8080 --name easystay-app ghcr.io/giuseppevarisano/easystay-backend:latest`

### Opzione B: In locale con IntelliJ
Se vuoi provare l'applicazione senza configurare un database MySQL:
1. Apri il progetto in IntelliJ.
2. Vai in **Edit Configurations**.
3. Nel campo **Active Profiles**, scrivi: `h2`.
4. Avvia l'applicazione.

---

## 📊 Database & Documentazione
L'applicazione utilizza un database in memoria **H2** popolato con record di test. Una volta avviata (tramite Docker porta 8081 o locale porta 8080), accedi a:

- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **H2 Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:easystaydb`
  - **User**: `sa`
  - **Password**: *(vuota)*