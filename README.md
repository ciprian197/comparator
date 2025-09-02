# Java Spring Boot Application for Comparing transaction files

## Steps to get started:

#### Prerequisites
- Maven
- Java 17

#### Build and run your app
- `mvn clean install`
    - builds the project and runs all tests
- `mvn package && java -jar target/comparator-0.0.1-SNAPSHOT.jar`
  ** Runs the application on port 8080

#### Test that your app is running
- `curl -X GET   http://localhost:8080/actuator/health`

#### Supported functionalities:
- Compare two transaction files (csv)
  - Two transactions are matching if they have the same id, type, date, amount and wallet reference
  - Two transactions are potential matching if they have the same id or the same date (with 2 mins tolerance) and amount (with 0.01 tolerance)
    - Potential matching between two transactions is managed by the `potentialMatchId` field in the transaction entry

#### Access and interact with the API using swagger at location http://localhost:8080/api-docs/swagger-ui/index.html