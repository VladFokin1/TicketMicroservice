FROM openjdk:22-jdk-slim
WORKDIR /app
COPY out/artifacts/TicketMicroservice_jar/TicketMicroservice.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]