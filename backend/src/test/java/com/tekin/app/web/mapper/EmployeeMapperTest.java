package com.tekin.app.web.mapper;

import com.tekin.app.domain.Employee;
import com.tekin.app.web.dto.EmployeeViewDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeMapperTest {

    @Test
    void coworkerSeesRedactedFields() {
        Employee e = new Employee(1L, "Test", "t@x", "Eng", "SE",
                new BigDecimal("1234"), LocalDate.of(2000,1,1));
        EmployeeViewDto dto = EmployeeMapper.toView(e, "COWORKER", 99L);
        assertNull(dto.salary());
        assertNull(dto.dob());
        assertEquals("Test", dto.name());
    }

    @Test
    void managerSeesEverything() {
        Employee e = new Employee(1L, "Test", "t@x", "Eng", "SE",
                new BigDecimal("1234"), LocalDate.of(2000,1,1));
        EmployeeViewDto dto = EmployeeMapper.toView(e, "MANAGER", 1L);
        assertNotNull(dto.salary());
        assertNotNull(dto.dob());
    }
}
