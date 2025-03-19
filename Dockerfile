# Stage 1: Build the application using Gradle
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Set the correct JAVA_HOME (specific to this Gradle image)
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Configure Gradle to use this Java version
RUN echo "org.gradle.java.home=$JAVA_HOME" >> /root/.gradle/gradle.properties

# Copy Gradle project files (to cache dependencies)
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY . .

# Run Gradle build (without building a JAR)
RUN gradle clean build --no-daemon

# Stage 2: Run the application with Gradle
FROM gradle:8.5-jdk17 AS runner

WORKDIR /app

# Set the correct JAVA_HOME for this stage too
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy the entire project from the builder stage to the runner stage
COPY --from=builder /app .

# Ensure Gradle knows where the correct Java home is
RUN echo "org.gradle.java.home=$JAVA_HOME" >> /root/.gradle/gradle.properties

# Expose the port the app will run on
EXPOSE 9091

# Run the Spring Boot app using Gradle's bootRun task
CMD ["gradle", "bootRun"]
