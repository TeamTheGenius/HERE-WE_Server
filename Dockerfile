FROM openjdk:17-jdk-slim

# curl 설치 추가
RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY build/libs/HereWeApplication.jar HereWeApplication.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "HereWeApplication.jar"]