# Use a base image with Java installed
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Java application JAR file into the container
COPY ./target/CosmosBackendApplication-0.0.1-SNAPSHOT.jar /app/CosmosBackendApplication-0.0.1-SNAPSHOT.jar

EXPOSE 8080
# Specify the command to run your application when the container starts
CMD ["java", "-jar", "CosmosBackendApplication-0.0.1-SNAPSHOT.jar"]
