package com.backend.stc.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DepartmentValidationService
{
    // Simulating a list of valid departments
    private static final List<String> VALID_DEPARTMENTS = Arrays.asList(
            "Engineering",
            "Marketing",
            "Sales",
            "Human Resources",
            "Finance"
    );

    // Simulating department validation by checking against the list of valid departments
    public boolean validateDepartment(String departmentName) {
        // Check if the department is valid (exists in the predefined list)
        return VALID_DEPARTMENTS.contains(departmentName);
    }
}
