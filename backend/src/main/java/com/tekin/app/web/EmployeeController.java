package com.tekin.app.web;

import com.tekin.app.domain.Employee;
import com.tekin.app.infra.EmployeeRepository;
import com.tekin.app.web.dto.EmployeeUpdateDto;
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
    public ResponseEntity<EmployeeViewDto> updateEmployee(@PathVariable("id") Long id,
                                                          @RequestBody EmployeeUpdateDto body,
                                                          HttpServletRequest req) {
        String role = RequestContext.role(req);
        Long requesterId = RequestContext.userId(req);
        boolean isManager = "MANAGER".equals(role);
        boolean isOwner   = "OWNER".equals(role) && requesterId != null && requesterId.equals(id);

        if (!(isManager || isOwner)) {
            return ResponseEntity.status(403).build();
        }

        return employeeRepository.findById(id).map(e -> {
            // full replace (or do null-checks if you want partial updates)
            e.setName(body.name());
            e.setEmail(body.email());
            e.setDepartment(body.department());
            e.setTitle(body.title());
            e.setSalary(body.salary());
            e.setDob(body.dob());
            employeeRepository.save(e);

            // return view using same redaction logic (manager/owner will see everything)
            EmployeeViewDto dto = EmployeeMapper.toView(e, role, requesterId);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }
}
