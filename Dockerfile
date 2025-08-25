# Use Eclipse Temurin Java 21 JRE (actively maintained)
FROM eclipse-temurin:21-jre-alpine

# Build arguments for artifact ID and version
ARG ARTIFACT_ID
ARG VERSION

# Install unzip utility
RUN apk add --no-cache unzip

# Set the working directory inside the container
WORKDIR /app

# Copy the ZIP distribution file from the target directory
COPY target/${ARTIFACT_ID}-${VERSION}.zip distribution.zip

# Extract the ZIP file and copy contents to app directory
RUN unzip distribution.zip && \
    mv ${ARTIFACT_ID}-${VERSION}.jar app.jar && \
    rm distribution.zip

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Run the Spring Boot application with external log4j2.xml configuration
ENTRYPOINT ["java", "-Dlog4j.configurationFile=log4j2.xml", "-jar", "app.jar"]
