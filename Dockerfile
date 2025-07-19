FROM openjdk:17
WORKDIR /app
COPY target/electronics-store.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
