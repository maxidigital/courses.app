FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn package -pl apps/courses.app -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/apps/courses.app/target/courses.app-1.12.jar app.jar
COPY apps/courses.app/config.properties /app/config.properties
COPY apps/courses.app/freedivemallorcaadmin-1a2eb9366fad.json /app/freedivemallorcaadmin-1a2eb9366fad.json

ENTRYPOINT ["java", "-jar", "app.jar"]
