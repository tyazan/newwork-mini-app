package com.tekin.app.web;

import com.tekin.app.domain.Employee;
import com.tekin.app.infra.EmployeeRepository;
import com.tekin.app.web.dto.EmployeeViewDto;
import com.tekin.app.web.mapper.EmployeeMapper;
import com.tekin.app.web.util.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeViewDto> getEmployee(@PathVariable("id") Long id, HttpServletRequest req) {
        String role = RequestContext.role(req);
        Long requesterId = RequestContext.userId(req);
        return employeeRepository.findById(id)
                .map(e -> ResponseEntity.ok(EmployeeMapper.toView(e, role, requesterId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long id,
                                                   @RequestBody Employee updated,
                                                   HttpServletRequest req) {
        String role = RequestContext.role(req);
        if (!"MANAGER".equalsIgnoreCase(role) && !"OWNER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }
        return employeeRepository.findById(id)
                .map(e -> {
                    e.setName(updated.getName());
                    e.setEmail(updated.getEmail());
                    e.setDepartment(updated.getDepartment());
                    e.setTitle(updated.getTitle());
                    e.setSalary(updated.getSalary());
                    e.setDob(updated.getDob());
                    return ResponseEntity.ok(employeeRepository.save(e));
                }).orElse(ResponseEntity.notFound().build());
    }
}
