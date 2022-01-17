FROM openjdk:11
WORKDIR application
ADD target/animal-name-service-*.jar animal-name-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","-Xms256m","-Xmx2048m","/application/animal-name-service.jar"]
