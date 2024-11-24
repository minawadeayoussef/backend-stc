Employee Management API with Email Notifications
This project is an Employee Management API built using Spring Boot. It integrates with third-party services for email and department validation, sends asynchronous email notifications, and handles errors gracefully using Resilience4J for retry and fallback mechanisms. The application implements a robust architecture, logging, and testing to meet the expectations of a Senior Java Developer.

Task Overview
The API allows users to manage employee data, validates certain fields via third-party services (such as email validation and department verification), and sends welcome emails to employees after they are created. The API ensures robust error handling and retry mechanisms when interacting with external services.

Core Features:
Employee Management: Create, retrieve, update, and delete employee records.
Asynchronous Email Notification: Send welcome emails asynchronously after an employee is created.
Third-Party Integration: Validate email and department using third-party APIs.
Fault Tolerance: Implement circuit breakers, retry, and rate limiting for interactions with external services.
Logging and Exception Handling: Log application events and handle errors with appropriate HTTP status codes.
Data Validation: Validate input data using @Valid annotations.
Testing: Write unit and integration tests to ensure functionality and error handling.
Table of Contents
API Endpoints
Data Model
Database
Exception Handling
Logging
Asynchronous Email Notifications
Third-Party API Integration
Testing
Setup and Installation
Advanced Features
API Endpoints
1. Create Employee
POST /api/employees
Accepts employee details, validates email and department with third-party APIs, and saves to the database.

Request Body:

json
Copy code
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 60000
}
Response:

json
Copy code
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 60000
}
2. Get Employee by ID
GET /api/employees/{id}
Retrieves employee details by their unique ID.

Response:

json
Copy code
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "department": "Engineering",
  "salary": 60000
}
3. Update Employee
PUT /api/employees/{id}
Updates an employee's details.

Request Body:

json
Copy code
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@newdomain.com",
  "department": "Marketing",
  "salary": 65000
}
Response:

json
Copy code
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@newdomain.com",
  "department": "Marketing",
  "salary": 65000
}
4. Delete Employee
DELETE /api/employees/{id}
Deletes an employee by ID.

Response:

json
Copy code
{
  "message": "Employee deleted successfully"
}
5. List All Employees
GET /api/employees
Retrieves a list of all employees.

Response:

json
Copy code
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "department": "Engineering",
    "salary": 60000
  }
]
