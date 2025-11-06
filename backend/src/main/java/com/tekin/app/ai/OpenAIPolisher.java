package com.tekin.app.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OpenAIPolisher implements TextPolisher {

    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;
    private final boolean enabled;

    private static final ObjectMapper M = new ObjectMapper();
    private final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final LocalPolisher fallback;

    public OpenAIPolisher(
            @Value("${app.ai.apiKey:}") String apiKey,
            @Value("${app.ai.baseUrl:https://api.openai.com/v1}") String baseUrl,
            @Value("${app.ai.chatModel:gpt-4o-mini}") String model,
            @Value("${app.ai.timeoutSeconds:30}") int timeoutSeconds,
            LocalPolisher fallback
    ) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.enabled = !this.apiKey.isEmpty();
        this.fallback = fallback;
    }

    @Override public String polish(String input) {
        if (!enabled) return fallback.polish(input);
        try {
            String body = buildBody(input);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            for (int attempt = 1; attempt <= 3; attempt++) {
                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                int sc = resp.statusCode();
                if (sc >= 200 && sc < 300) {
                    String out = parse(resp.body());
                    return (out == null || out.isBlank()) ? fallback.polish(input) : out.trim();
                }
                if (sc == 429 || sc == 503) { Thread.sleep(500L * attempt); continue; }
                System.err.println("[OpenAI] HTTP " + sc + ": " + resp.body());
                break;
            }
        } catch (Exception e) {
            System.err.println("[OpenAI] call failed: " + e.getMessage());
        }
        return fallback.polish(input);
    }

    private String buildBody(String userText) {
        ObjectNode root = M.createObjectNode();
        root.put("model", model);
        ArrayNode msgs = root.putArray("messages");
        msgs.add(msg("system","You are a grammar and clarity editor. Rewrite user text with correct grammar, punctuation and tone. Return only the corrected text."));
        msgs.add(msg("user", userText));
        root.put("temperature", 0.2);
        return root.toString();
    }

    private ObjectNode msg(String role, String content) {
        ObjectNode n = M.createObjectNode();
        n.put("role", role); n.put("content", content);
        return n;
    }

    private String parse(String body) {
        try {
            JsonNode n = M.readTree(body);
            JsonNode content = n.path("choices").path(0).path("message").path("content");
            if (content.isTextual()) return content.asText();
        } catch (Exception e) {
            System.err.println("[OpenAI] parse error: " + e.getMessage());
        }
        return null;
    }
}
