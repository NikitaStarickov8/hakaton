FROM maven:3.8.4-openjdk-17 as maven-builder
COPY .. /app/src
COPY ../../pom.xml /app

RUN mvn -f /app/pom.xml clean package -DskipTests
FROM openjdk:17-alpine

COPY --from=maven-builder app/target/Hakaton-0.0.1-SNAPSHOT.jar /app-service/Hakaton-0.0.1-SNAPSHOT.jar
WORKDIR /app-service

EXPOSE 8080
ENTRYPOINT ["java","-jar","Hakaton-0.0.1-SNAPSHOT.jar"]