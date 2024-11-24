Employee Management API with Email Notification
This is an Employee Management API built using Spring Boot and JDK 21. The API allows managing employee information and also sends welcome emails asynchronously upon employee creation. The email sending is optimized using Resilience4J for fault tolerance, with features like circuit breaking, retry, and rate limiting.

Features
Employee Management: Create, read, update, and delete employee records.
Asynchronous Email Sending: Sends email notifications asynchronously using JDK 21 Virtual Threads.
Fault Tolerance: Uses Resilience4J for retry, circuit breaking, and rate limiting for email notifications.
Validation: Includes input validation for creating employees and sending emails.
Prerequisites
JDK 21 : Ensure that you are using JDK 21 to fully leverage Virtual Threads.
Maven: This project uses Maven for dependency management.
Spring Boot: Version 3.x or higher.
