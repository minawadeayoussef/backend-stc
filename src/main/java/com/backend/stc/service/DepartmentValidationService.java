package com.backend.stc.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DepartmentValidationService
{
    private static final List<String> VALID_DEPARTMENTS = Arrays.asList(
            "Engineering",
            "Marketing",
            "Sales",
            "Human Resources",
            "Finance"
    );

    public boolean validateDepartment(String departmentName) {
        return VALID_DEPARTMENTS.contains(departmentName);
    }
}
