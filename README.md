# Employee Management Service

## Overview
This project provides a RESTful API for managing employee data, supporting CRUD operations and employee birthday lookup by month. Need to maintain and retrieve employee information, especially to find those with birthdays in a given month to recognize their special day.

The project uses:
- **Java 21**
- **Spring Boot 3.3.5** for application setup
- **Spring Data JPA** and **Hibernate** for database operations
- **H2 Database** (in-memory) for development and testing purposes
- **Spring Boot Starter Actuator**: To expose useful endpoints for monitoring the application.
- **Spring Boot Starter Test**: For unit and integration testing.


## Features
- **CRUD Operations**: Create, Read, Update, Delete employee records.
- **Birthday Filter**: Retrieve employees based on their birthday month.
- **RESTful API**: Interacts with JSON requests and responses.
- **In-memory H2 Database**: Stores employee information (easily replaceable with other databases).

## Technologies Used
- **Java 21**
- **Spring Boot** (3.3.5)
- **Spring Data JPA**: Manages the persistence layer.
- **H2 Database**: Lightweight, in-memory database for development.
- **Maven**: Dependency and build management.

## Prerequisites
- **Java 21** or higher
- **Maven** installed
- **Git** (if cloning from a repository)

## Setup Instructions
1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd employee-management-api
    ```
2. **Build the project:**
```bash
mvn clean install
```

3. **Run the Application Use Maven to start the Spring Boot application:**
```bash
mvn spring-boot:run
```

Alternatively, you can package the application as a JAR and run it:

 ```bash
mvn clean package
java -jar target/employee-management-api-0.0.1-SNAPSHOT.jar
```

4. **Access the API The service runs on http://localhost:8080 by default.**

### API Endpoints

| Method | Endpoint | Description |
|-----------------|-----------------|-----------------|
| POST | /api/employees | Create a new employee |
| GET | /api/employees | Get all employees |
| GET | /api/employees/{id} | Get an employee by ID |
| PUT | /api/employees/{id} | Update an employee by ID |
| DELETE | /api/employees/{id} | Delete an employee by ID |
| GET | /api/employees?month={month} | Get employees with birthdays in a given month |
| POST | /api/employees/upload/csvFromResourcesFolder | Upload CSV file from resources folder |
| POST | /api/employees/upload/csvFromFileSystem | Upload CSV file from file system |


### Example Requests

Create a New Employee

 ```http
POST /api/employees
Content-Type: application/json

{
  "First name": "Luisa",
  "Last name": "Brakus",
  "Location": "San Diego, CA"
  "Birthday": "1/30/2001"
}
 ```

Get Employees with July Birthdays

```http
GET /api/employees?month=7
 ```


## Data Model

The Employee entity includes:

- id: Unique identifier (auto-generated).
- first_name: Employee’s first name.
- first_name: Employee’s last name.
- location: Location of the employee.
- city: City
- state: state
- birth_day: birthDay of the employee

## CSV Data Import

Place the provided `ProgrammingChallengeData.csv` file in the `src/main/resources` folder. On application startup, the service can read and populate data from this CSV (optional, depending on requirements).

## Testing Strategy

- Unit Testing: Tests individual methods using JUnit and Mockito.
- Integration Testing: Validates API endpoints and JPA operations with in-memory H2 database.
- Postman Collection: [Optional] Use Postman to verify the API responses.


## Example Usage with cURL

Add an Employee

```bash
curl -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d '{
    "First Name": "Luisa",
    "Last Name": "Brakus",
    "Location": "San Diego, CA",
    "birthday": "1/30/2001"
}'
```

Get Employees by Birthday Month

```bash
curl http://localhost:8080/api/employees?month=5
```

## Running Tests

### Unit Tests
Unit tests are written using JUnit 5 and Spring Boot Test. You can run the tests using Maven:
```bash
mvn test
```

### Integration Tests
The integration tests ensure that the API is working as expected by sending HTTP requests to the endpoints and checking responses.

## Monitoring and Health Check

This application uses Spring Boot Actuator for monitoring and health checks. The following endpoints are available:

- Health check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`


## Possible Enhancements

-Database Support: Switch from H2 to MySQL, MongoDB, PostgreSQL, etc.
- Validation: Add more robust validation for fields such as birthday.
- Pagination and Sorting: Add support for paginated and sorted results for large datasets.
