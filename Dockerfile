FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/getout-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]