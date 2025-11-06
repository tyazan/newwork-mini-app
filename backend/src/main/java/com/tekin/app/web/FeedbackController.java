package com.tekin.app.web;

import com.tekin.app.ai.AiPolishService;
import com.tekin.app.ai.TextPolisher;
import com.tekin.app.domain.Feedback;
import com.tekin.app.infra.FeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/{id}/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final TextPolisher polisher;


    public FeedbackController(FeedbackRepository feedbackRepository, TextPolisher textPolisher) {
        this.feedbackRepository = feedbackRepository;
        this.polisher = textPolisher;
    }

    @GetMapping
    public List<Feedback> list(@PathVariable("id") Long id) {
        return feedbackRepository.findByEmployeeIdOrderByCreatedAtDesc(id);
    }

    @PostMapping
    public ResponseEntity<Feedback> add(@PathVariable("id") Long id,
                                        @RequestBody Map<String,Object> body,
                                        jakarta.servlet.http.HttpServletRequest req) {

        String text = String.valueOf(body.getOrDefault("text", ""));
        Object p = body.get("polish");
        boolean polish = (p instanceof Boolean && (Boolean)p) || "true".equalsIgnoreCase(String.valueOf(p));
        String finalText = polish ? polisher.polish(text) : text;


        long author = 0L;
        try { author = Long.parseLong(String.valueOf(req.getHeader("X-Demo-UserId"))); } catch (Exception ignored) {}

        Feedback f = new Feedback(
                null, id, author,
                text,
                finalText,
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(201).body(feedbackRepository.save(f));
    }

}
