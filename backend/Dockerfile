# Etapa 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Ejecutamos clean para asegurar que no haya basura de compilaciones locales
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Argumento para el nombre del archivo JAR (opcional pero profesional)
COPY --from=build /app/target/reserva-demo-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto que usa Google Cloud Run por defecto
EXPOSE 8080

# Forzamos el perfil prod y nos aseguramos de que escuche en el puerto que asigne Google ($PORT)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]