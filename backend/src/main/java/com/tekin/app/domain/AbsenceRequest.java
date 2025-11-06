package com.tekin.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AbsenceRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    @Enumerated(EnumType.STRING)
    private AbsenceStatus status;


}
