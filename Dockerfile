FROM eclipse-temurin:17-jre-alpine

COPY ./role-registry-application/target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
