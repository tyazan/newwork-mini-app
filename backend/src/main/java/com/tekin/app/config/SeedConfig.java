package com.tekin.app.config;

import com.tekin.app.domain.*;
import com.tekin.app.infra.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner seed(EmployeeRepository empRepo,
                           FeedbackRepository fbRepo) {
        return args -> {
            if (empRepo.count() == 0) {
                Employee e1 = new Employee(null, "Alice Manager", "alice@corp.example", "Engineering",
                        "Engineering Manager", new BigDecimal("95000"), LocalDate.of(1988, 4, 12));
                Employee e2 = new Employee(null, "Bob Owner", "bob@corp.example", "Engineering",
                        "Senior Engineer", new BigDecimal("75000"), LocalDate.of(1992, 9, 30));
                Employee e3 = new Employee(null, "Carol Coworker", "carol@corp.example", "Design",
                        "Product Designer", new BigDecimal("68000"), LocalDate.of(1995, 2, 5));
                empRepo.save(e1);
                empRepo.save(e2);
                empRepo.save(e3);

                fbRepo.save(new Feedback(null, e2.getId(), e1.getId(), "Great collaboration on Q3 goals.", null, LocalDateTime.now().minusDays(3)));
                fbRepo.save(new Feedback(null, e2.getId(), e3.getId(), "Nice refactor of the auth module.", null, LocalDateTime.now().minusDays(1)));
            }
        };
    }
}
