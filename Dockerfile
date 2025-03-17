FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --upgrade --no-cache expat  # fix CVE-2024-8176

COPY ./role-registry-application/target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
