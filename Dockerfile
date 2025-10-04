# -------- Build stage --------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build (uden tests)
COPY src ./src
RUN mvn -q -DskipTests package

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiér jar fra build
COPY --from=build /app/target/*.jar app.jar

# Spring Boot port
EXPOSE 8080

# Valgfri JVM-flags
ENV JAVA_OPTS=""

# Start app
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
