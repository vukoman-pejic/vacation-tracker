FROM openjdk:18
EXPOSE 8080
ADD target/vacation-tracker-0.0.1-SNAPSHOT.jar data-import-admin.jar
ADD ./src/main/resources/samples src/main/resources/samples
ENTRYPOINT ["java","-jar","/data-import-admin.jar"]