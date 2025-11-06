package com.tekin.app.web;

import com.tekin.app.domain.AbsenceRequest;
import com.tekin.app.domain.AbsenceStatus;
import com.tekin.app.infra.AbsenceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees/{id}/absences")
public class AbsenceController {

    private final AbsenceRepository repo;

    public AbsenceController(AbsenceRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<AbsenceRequest> create(@PathVariable Long id, @RequestBody AbsenceRequest req) {
        req.setId(null);
        req.setEmployeeId(id);
        req.setStatus(AbsenceStatus.REQUESTED);
        return ResponseEntity.status(201).body(repo.save(req));
    }
}
