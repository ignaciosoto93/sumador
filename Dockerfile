# Use the official gradle image to build the application
FROM gradle:latest AS build

# Set the working directory
WORKDIR /app

# Copy the build.gradle and settings.gradle files to the container
COPY build.gradle .
COPY settings.gradle .

# Copy the source code to the container
COPY src/ src/

# Build the application
RUN gradle build --no-daemon -x test

# Use a slim OpenJDK image to run the application
FROM adoptopenjdk/openjdk11:alpine-jre
# Set the working directory
WORKDIR /app

# Copy the built jar file from the build container to the application container
COPY --from=build /app/build/libs/*.jar app.jar

# Start the application
CMD ["java", "-jar", "app.jar"]
