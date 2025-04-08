FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/HereWeApplication.jar HereWeApplication.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "HereWeApplication.jar"]