FROM openjdk:11-jre-slim

COPY ./role-registry-application/target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
