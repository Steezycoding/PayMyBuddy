# PayMyBuddy

## Description
**PayMyBuddy** is an app that lets users transfer money to their friends with ease.

## Prerequisites:
- **Java**: JDK 21 (OpenJDK 21 has been used to dev)
- **Maven**: 3.9.x (version 3.9.7 included with [Maven Wrapper](.mvn/wrapper/maven-wrapper.properties))
- **Docker** _[+ docker-compose]_
- _Docker_ **MySQL LTS image** (pulled automatically at first application run with _dev_ profile)

## Technical stack / Dependencies
Here is the stack / dependencies used by the project.
- Spring Boot 3.3.1 with following starters / dependencies:
  - Web 
  - Data JPA
  - Spring Security
  - Thymeleaf
  - Actuator
  - Docker Compose
- MySQL connector
- Lombok
- OpenAPI _(with Swagger UI)_

## <a id="app-install"></a>Installation / Usage

_This project is using Maven Wrapper to facilitate Maven version compatibility.
All Maven commands should be executed with `./mvnw`_

1. **Clone the Git repository**:
   ```sh
   git clone git@github.com:Steezycoding/paymybuddy.git
   ```
   *or*
   ```sh
   git clone https://github.com/Steezycoding/paymybuddy.git
   ```
   **then**
   ```sh
   cd springboot-app
   ```
2. **Please give a check to the** [DATABASE.md](docs/database/DATABASE.md/scripts/database/DATABASE.md) **file located in _scripts/database_ folder**


3. **Run app (with _dev_ profile) to enjoy development :**
    ```sh
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```
   If you are using JetBrains IntelliJ IDE, a `Run Dev (webapp)` run configuration is provided under the _.idea/runConfigurations_ folder.


4. **Endpoints**:

   API and actuators endpoints documentation is generated and reachable on **runtime**
   at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
