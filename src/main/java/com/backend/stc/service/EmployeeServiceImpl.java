package com.backend.stc.service;

import com.backend.stc.dto.EmailRequest;
import com.backend.stc.exception.EmployeeNotFoundException;
import com.backend.stc.model.Employee;
import com.backend.stc.repository.EmployeeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentValidationService departmentValidationService;
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentValidationService departmentValidationService, EmailNotificationService emailNotificationService) {
        this.employeeRepository = employeeRepository;
        this.departmentValidationService = departmentValidationService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackMethod")
    @Retry(name = "default", fallbackMethod = "retryFallback")
    @RateLimiter(name = "default", fallbackMethod = "rateLimiterFallback")
    public Employee createEmployee(Employee employee) {
        // Validate department before saving employee
        if (!departmentValidationService.validateDepartment(employee.getDepartment())) {
            throw new IllegalArgumentException("Invalid department: " + employee.getDepartment());
        }

        // Save the employee to the database
        Employee savedEmployee = employeeRepository.save(employee);

        // Dynamically create the email subject and body
        String emailSubject = "Welcome to the company, " + savedEmployee.getFirstName() + "!";
        String emailBody = "Dear " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName() + ",\n\n" +
                "Welcome aboard! We are excited to have you as part of our team in the " + savedEmployee.getDepartment() + " department.\n\n" +
                "Your email: " + savedEmployee.getEmail() + "\n" +
                "We look forward to working with you!\n\n" +
                "Best regards,\n" +
                "The Company Team";

        EmailRequest emailRequest = new EmailRequest(savedEmployee.getEmail(), emailSubject, emailBody);
        emailNotificationService.sendEmail(emailRequest);

        return savedEmployee;
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        // Retrieve the employee by ID from the database
        return employeeRepository.findById(id);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        // Check if the employee exists before updating
        if (employeeRepository.existsById(id)) {
            employee.setId(id); // Ensure the existing ID is retained
            return employeeRepository.save(employee); // Update the employee in the database
        } else {
            throw new RuntimeException("Employee not found");
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        // Check if the employee exists before deleting
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id); // Delete the employee from the database
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }

    @Override
    public List<Employee> listAllEmployees() {
        // Retrieve all employees from the database
        return employeeRepository.findAll();
    }

    public String fallbackMethod(Throwable t) {
        return "Fallback due to: " + t.getMessage();
    }

    // Fallback for Retry
    public String retryFallback(Throwable t) {
        return "Retry failed: " + t.getMessage();
    }

    // Fallback for Rate Limiter
    public String rateLimiterFallback(Throwable t) {
        return "Rate limit exceeded: " + t.getMessage();
    }
}
