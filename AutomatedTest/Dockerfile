FROM openjdk:11-jre-slim

WORKDIR /app
RUN chgrp -R 0 /app && \
    chmod -R g=u /app

COPY ./target/cso-test-runner.jar .

ENTRYPOINT ["java","-Xss2048k","-jar","cso-test-runner.jar"]