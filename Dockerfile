FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY /target/*.jar totp-authentication-demo-0.0.1-SNAPSHOT.jar
RUN mkdir -p /config
ENTRYPOINT ["java","-jar","/totp-authentication-demo-0.0.1-SNAPSHOT.jar"]
