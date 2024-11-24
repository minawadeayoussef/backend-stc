package com.backend.stc.controller;


import com.backend.stc.exception.EmployeeNotFoundException;
import com.backend.stc.model.Employee;
import com.backend.stc.service.EmailNotificationServiceImpl;
import com.backend.stc.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController
{

    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);


    @Autowired // Optional, Spring will inject automatically in most cases
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        CompletableFuture<Employee> createdEmployeeFuture = employeeService.createEmployee(employee);

        // Blocking call to wait for the result (you can use .join() to block until the result is available)
        Employee createdEmployee = createdEmployeeFuture.join();

        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);

        return employee
                .map(emp -> {
                    logger.info("Employee found with id: {}", id); // Log found employee
                    return new ResponseEntity<>(emp, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    logger.warn("Employee not found with id: {}", id); // Log not found employee
                    throw new EmployeeNotFoundException("Employee not found with id: " + id);
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> listAllEmployees() {
        List<Employee> employees = employeeService.listAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
