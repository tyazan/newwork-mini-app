package com.tekin.app.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class AiConfig {
    @Value("${app.ai.apiKey:}") private String apiKey;
    @Autowired private LocalPolisher localPolisher;
    @Autowired private OpenAIPolisher openAIPolisher;

    @Bean @Primary
    public TextPolisher textPolisher() {
        return (apiKey != null && !apiKey.isBlank()) ? openAIPolisher : localPolisher;
    }
}
