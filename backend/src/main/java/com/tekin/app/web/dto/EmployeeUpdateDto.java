package com.tekin.app.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeUpdateDto(
        String name,
        String email,
        String department,
        String title,
        BigDecimal salary,
        LocalDate dob
) {}
