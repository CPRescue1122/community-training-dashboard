# Use the official OpenJDK image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Build the app (assuming Maven wrapper is used)
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/community-training-dashboard-0.0.1-SNAPSHOT.jar"]
