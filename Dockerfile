FROM eclipse-temurin:11-jre-jammy

COPY ./role-registry-application/target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
