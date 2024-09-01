FROM openjdk:11-jdk-slim

WORKDIR /app-challenge

COPY mvnw .

COPY .mvn .mvn

COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN chmod +x ./mvnw

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]

