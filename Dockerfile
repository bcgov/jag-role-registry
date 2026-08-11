#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests 

#############################################################################################
#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################

FROM eclipse-temurin:17-jre-alpine

# fix CVE-2025-65018, CVE-2025-64720, CVE-2025-66293, CVE-2025-59375, CVE-2025-9230
# fix CVE-2026-2100 (p11-kit / p11-kit-trust)
RUN apk update \
    && apk add --upgrade --no-cache libexpat \
    && apk add --upgrade --no-cache libpng \
    && apk add --upgrade --no-cache openssl \
    && apk add --upgrade --no-cache p11-kit p11-kit-trust

COPY --from=build /app/role-registry-application/target/cso-application.jar cso-application.jar

ENTRYPOINT ["java","-jar","/cso-application.jar"]
