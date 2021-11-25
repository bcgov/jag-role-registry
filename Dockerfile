FROM openjdk:11-jre-slim

COPY ./target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
