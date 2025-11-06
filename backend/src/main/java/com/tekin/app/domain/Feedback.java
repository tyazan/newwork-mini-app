package com.tekin.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private Long authorId;
    @Column(length = 4000)
    private String text;
    @Column(length = 4000)
    private String polishedText;
    private LocalDateTime createdAt;


}
