package com.tekin.app.web.mapper;

import com.tekin.app.domain.Employee;
import com.tekin.app.web.dto.EmployeeViewDto;

public class EmployeeMapper {

    public static EmployeeViewDto toView(Employee e, String role, Long requesterId) {
        boolean isManagerOrOwner = "MANAGER".equalsIgnoreCase(role) || "OWNER".equalsIgnoreCase(role);
        if (isManagerOrOwner) {
            return new EmployeeViewDto(e.getId(), e.getName(), e.getEmail(), e.getDepartment(), e.getTitle(), e.getSalary(), e.getDob());
        }
        // COWORKER: redact sensitive fields
        return new EmployeeViewDto(e.getId(), e.getName(), e.getEmail(), e.getDepartment(), e.getTitle(), null, null);
    }
}
