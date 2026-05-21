FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY qualitywatch-backend/pom.xml qualitywatch-backend/
COPY qualitywatch-agent/pom.xml qualitywatch-agent/
COPY qualitywatch-backend/src qualitywatch-backend/src
COPY qualitywatch-agent/src qualitywatch-agent/src
RUN mvn -q -pl qualitywatch-backend -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /build/qualitywatch-backend/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl -fsS http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
