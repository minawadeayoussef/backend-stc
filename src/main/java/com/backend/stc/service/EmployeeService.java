package com.backend.stc.service;

import com.backend.stc.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public interface EmployeeService
{

        // Method to create a new employee
        CompletableFuture<Employee> createEmployee(Employee employee);

        // Method to get an employee by ID
        Optional<Employee> getEmployeeById(Long id);

        // Method to update an existing employee
        Employee updateEmployee(Long id, Employee employee);

        // Method to delete an employee by ID
        void deleteEmployee(Long id);

    List<Employee> listAllEmployees();

}



