FROM openjdk:8-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=sgdoc-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
CMD java -Dspring.profiles.active=prod -jar /app.jar
