# jag-role-registry

[![Lifecycle:Maturing](https://img.shields.io/badge/Lifecycle-Maturing-007EC6)](https://github.com/bcgov/jag-role-registry)
[![Maintainability](https://api.codeclimate.com/v1/badges/5a7027d5cc5800eeb2fe/maintainability)](https://codeclimate.com/github/bcgov/jag-role-registry/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/5a7027d5cc5800eeb2fe/test_coverage)](https://codeclimate.com/github/bcgov/jag-role-registry/test_coverage)
### Recommended Tools
* Intellij
* Docker
* Maven
* Java 17
* Lombok
* Redis

### Application Endpoints

Local Host: http://127.0.0.1:8080

WSDL Endpoint Local: http://localhost:8080/ws/RoleRegistry.Source.RoleRegistry.ws.provider:RoleRegistry?WSDL

Actuator Endpoint Local: http://localhost:8080/actuator/health

Code Climate: https://codeclimate.com/github/bcgov/jag-role-registry

### Required Environmental Variables

BASIC_AUTH_PASS: The password for the basic authentication. This can be any value for local.

BASIC_AUTH_USER: The username for the basic authentication. This can be any value for local.

ORDS_HOST: The url for ords rest package.

ORDS_USERNAME: ORDS_HOST authentication

ORDS_PASSWORD: ORDS_HOST authentication

REDIS_HOST: Redis url, 'localhost' by default if redis is installed on the local device

REDIS_PORT: Redis port, 6379 by default

REDIS_AUTH_PASS: Redis password for authentication

REDIS_TTL_SEC: the time to live for key being set in redis. When the TTL elapses, the key is automatically destroyed.

CACHING: enable/disable redis caching.

### Optional Enviromental Variables
SPLUNK_HTTP_URL: The url for the splunk hec.

SPLUNK_TOKEN: The bearer token to authenticate the application.

SPLUNK_INDEX: The index that the application will push logs to. The index must be created in splunk
before they can be pushed to.

### Building the Application
1) Make sure using java 17 for the project modals and sdk
2) Run ``mvn compile``
3) Make sure ```role-registry-common-models``` is marked as generated sources root (xjc)

### Pre-running application
* Install and run Redis
* Run ```sudo docker pull redis```
* Run ```docker run --name redis -d -p 6379:6379 redis redis-server --requirepass ***```

### Running the application
Via IDE
1) Set env variables.
2) Run the application

Via Jar
1) Run ```mvn package```
2) Run ```java -jar ./target/cso-application.jar $ENV_VAR$```  (Note that $ENV_VAR$ are environment variables)

Via Docker
1) Run ```mvn package```
2) Run ```docker build -t role-reg-api .``` from root folder
3) Run ```docker run -p 8080:8080 role-reg-api $ENV_VAR$```  (Note that $ENV_VAR$ are environment variables)

### Pre Commit
1) Do not commit \CRLF use unix line enders
2) Run the linter ```mvn spotless:apply```

### JaCoCo Coverage Report
1) Run ```mvn test```
2) Run ```mvn jacoco:report```
3) Open ```target/site/jacoco/index.html``` in a browser
