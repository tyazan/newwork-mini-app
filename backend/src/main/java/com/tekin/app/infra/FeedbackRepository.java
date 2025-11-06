package com.tekin.app.infra;

import com.tekin.app.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
