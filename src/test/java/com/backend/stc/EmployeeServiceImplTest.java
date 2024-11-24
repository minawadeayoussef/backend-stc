package com.backend.stc;

import com.backend.stc.dto.EmailRequest;
import com.backend.stc.exception.EmployeeNotFoundException;
import com.backend.stc.model.Employee;
import com.backend.stc.repository.EmployeeRepository;
import com.backend.stc.service.DepartmentValidationService;
import com.backend.stc.service.EmailNotificationService;
import com.backend.stc.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentValidationService departmentValidationService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department("HR")
                .build();
    }

    @Test
    void createEmployee_validEmployee_shouldCreateEmployeeAndSendEmail() throws Exception {
        // Arrange
        when(departmentValidationService.validateDepartment(anyString())).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        // Act
        CompletableFuture<Employee> result = employeeService.createEmployee(employee);

        result.get();

        // Assert
        verify(employeeRepository, times(1)).save(employee);
        verify(emailNotificationService, times(1)).sendEmail(any(EmailRequest.class));
    }

    @Test
    void createEmployee_invalidDepartment_shouldThrowIllegalArgumentException() {
        // Arrange
        when(departmentValidationService.validateDepartment(anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(employee);
        });
        assertEquals("Invalid department: HR", exception.getMessage());
    }

    @Test
    void getEmployeeById_existingEmployee_shouldReturnEmployee() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals(employee, result.get());
    }

    @Test
    void getEmployeeById_nonExistingEmployee_shouldReturnEmpty() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateEmployee_existingEmployee_shouldUpdateEmployee() {
        // Arrange
        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("IT")
                .build();

        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertEquals(updatedEmployee.getFirstName(), result.getFirstName());
        assertEquals(updatedEmployee.getLastName(), result.getLastName());
        assertEquals(updatedEmployee.getEmail(), result.getEmail());
    }

    @Test
    void updateEmployee_nonExistingEmployee_shouldThrowException() {
        // Arrange
        Employee updatedEmployee = Employee.builder()
                .id(999L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .department("IT")
                .build();

        when(employeeRepository.existsById(anyLong())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployee(999L, updatedEmployee);
        });
        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void deleteEmployee_existingEmployee_shouldDeleteEmployee() {
        when(employeeRepository.existsById(anyLong())).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_nonExistingEmployee_shouldThrowEmployeeNotFoundException() {
        when(employeeRepository.existsById(anyLong())).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployee(999L);
        });
        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void listAllEmployees_shouldReturnListOfEmployees() {
        List<Employee> employees = List.of(employee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.listAllEmployees();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(employee, result.get(0));
    }

    @Test
    void fallbackMethod_shouldReturnFallbackMessage() {
        String errorMessage = "Service failure";

        String result = employeeService.fallbackMethod(new RuntimeException(errorMessage));

        assertEquals("Fallback due to: " + errorMessage, result);
    }

    @Test
    void retryFallback_shouldReturnRetryFallbackMessage() {
        String errorMessage = "Retry failure";

        String result = employeeService.retryFallback(new RuntimeException(errorMessage));

        assertEquals("Retry failed: " + errorMessage, result);
    }

    @Test
    void rateLimiterFallback_shouldReturnRateLimiterFallbackMessage() {
        String errorMessage = "Rate limit exceeded";

        String result = employeeService.rateLimiterFallback(new RuntimeException(errorMessage));

        assertEquals("Rate limit exceeded: " + errorMessage, result);
    }
}
