FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENV FIRESTORE_EMULATOR_HOST=host.docker.internal:8085
ENTRYPOINT ["java", "-jar", "app.jar"]
