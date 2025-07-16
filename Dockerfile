FROM maven:3.9.10 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
<<<<<<< Updated upstream
COPY --from=build /app/target/store_and_forward_service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"] 
=======
COPY --from=build /app/target/aml-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
>>>>>>> Stashed changes
