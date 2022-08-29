FROM openjdk:18
EXPOSE 8080
ADD target/data-import-admin-0.0.1-SNAPSHOT.jar data-import-admin.jar
ENTRYPOINT ["java","-jar","/data-import-admin.jar"]