# Use maven to compile the java application.
FROM docker.io/maven AS build-env

# Set the working directory to /app
WORKDIR /app

# copy the pom.xml file to download dependencies
COPY pom.xml ./

# download dependencies as specified in pom.xml
# building dependency layer early will speed up compile time when pom is unchanged
RUN mvn verify --fail-never -DskipTests -Djdk.tls.client.protocols=TLSv1.2

# Copy the rest of the working directory contents into the container
COPY . ./

# Compile the application.
RUN mvn -Dmaven.test.skip=true  -Djdk.tls.client.protocols=TLSv1.2 package

# Build runtime image.
FROM openjdk:11.0.8

# Copy the compiled files over.
COPY --from=build-env /app/target/ /app/

# Starts java app with debugging server at port 5005.
CMD ["java", "-jar", "/app/hello-world-1.0.0.jar", "-Djdk.tls.client.protocols=TLSv1.2"]
