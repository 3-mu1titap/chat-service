
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/chat-0.0.1-SNAPSHOT.jar chat-service.jar

EXPOSE 9600

ENTRYPOINT ["java", "-jar", "chat-service.jar"]

ENV TZ=Asia/Seoul