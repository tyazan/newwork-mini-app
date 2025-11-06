package com.tekin.app.infra;

import com.tekin.app.domain.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<AbsenceRequest, Long> {
}
