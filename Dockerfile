FROM maven:3.9-eclipse-temurin-17 AS builder
ARG GITHUB_TOKEN
WORKDIR /build

# Install underwater dependencies into local Maven cache
RUN git clone https://maxidigital:${GITHUB_TOKEN}@github.com/maxidigital/underwater.git
RUN mvn install -f underwater/pom.xml -DskipTests -B

# Build courses.app
COPY . courses.app/
RUN mvn package -f courses.app/pom.xml -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /build/courses.app/target/courses.app-1.12.jar app.jar
COPY docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
