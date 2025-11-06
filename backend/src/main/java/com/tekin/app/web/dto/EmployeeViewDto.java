package com.tekin.app.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeViewDto(Long id, String name, String email, String department, String title,
                              BigDecimal salary, LocalDate dob) { }
