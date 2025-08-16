# ---- Build Stage ----
# Use an image that has both Java 17 and Maven
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the parent pom.xml from the root of the backend directory
COPY pom.xml .
# Copy all the service-specific pom.xml files to ensure all dependencies can be resolved
COPY ./api-gateway/pom.xml ./api-gateway/
COPY ./service-discovery/pom.xml ./service-discovery/
COPY ./user-service/pom.xml ./user-service/
COPY ./wallet-service/pom.xml ./wallet-service/
COPY ./transaction-service/pom.xml ./transaction-service/
COPY ./notification-service/pom.xml ./notification-service/

# This Dockerfile is a template. We will specify which service to build
# using a build argument.
ARG SERVICE_PATH
COPY ${SERVICE_PATH}/src ./${SERVICE_PATH}/src

# Run the Maven package command, but skip test compilation and execution entirely.
RUN mvn -pl ${SERVICE_PATH} -am clean package -Dmaven.test.skip=true

# ---- Final Stage ----
# Use a lightweight Java 17 image for the final application
FROM openjdk:17-jdk-slim

# Set a volume for temporary files
VOLUME /tmp

# Copy the .jar file created in the 'build' stage for the specific service
ARG SERVICE_PATH
COPY --from=build /app/${SERVICE_PATH}/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java","-jar","/app.jar"]