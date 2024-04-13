# TOTP Demo API

It's a Java 17 microservice API demo project, build with Spring Boot 3. <br />
This microservice API is responsible to generate a TOTP QRCode and validate a given TOTP code.

The program creates a user in memory, with a generated secret key used to generate the TOTP.

### Version
1.0.0

## Resources
* [Spring](https://spring.io)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/)
* [TOTP](https://datatracker.ietf.org/doc/html/rfc6238)
* [H2 Database](https://www.h2database.com/html/main.html)

## Getting Started

These instructions will guide you to copy the project from the repository and run it.

### Prerequisites

Things you need to have installed:

```
* Java 17
* Maven
```

### Local Installation

Basically clone the project from the remote repository to the local machine, using the git commands.

```
$git clone [URL].git
```

### Run

To run the project just use an IDE or use the java command

```
$mvn clear package
$java -jar /target/totp-authentication-demo-0.0.1-SNAPSHOT.jar
```

### Documentation
* [API Documentation](http://localhost:8081/api/totp/docs/index.html)
* [Health Check](http://localhost:8081/api/totp/actuator/health)

### Tests
To run the unit tests and the integration tests run the following command:

```
$mvn clean verify
```

## Authors
**BEOM &copy; since 2024**