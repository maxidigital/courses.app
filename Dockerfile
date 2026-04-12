FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
ARG GITHUB_TOKEN
RUN mvn package -DskipTests -B \
    -s settings.xml \
    -DGITHUB_TOKEN=${GITHUB_TOKEN}

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/courses.app-1.12.jar app.jar
COPY docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
