# Usa una base Java 17
FROM eclipse-temurin:17-jdk-alpine

# Crea una cartella per l'app
WORKDIR /app

# Copia il file .jar generato da Maven nel container
COPY target/*.jar app.jar

# Espone la porta 8080 (interna al container)
EXPOSE 8080

# MODIFICA QUI: Aggiungiamo il flag per il profilo test
ENTRYPOINT ["java", "-Dspring.profiles.active=h2", "-jar", "app.jar"]