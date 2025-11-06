package com.tekin.app.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class AiPolishService {

    @Value("${app.ai.hfToken:}")
    private String hfToken;

    // good defaults for polishing
    @Value("${app.ai.model:vennify/t5-base-grammar-correction}")
    private String model;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String maybePolish(String text, boolean polishRequested) {
        if (!polishRequested) return text;
        if (hfToken == null || hfToken.isBlank()) return localPolish(text);

        String payload = MAPPER.createObjectNode()
                .put("inputs", text) // T5 grammar models handle raw text fine; a "grammar:" prefix is optional
                .set("parameters", MAPPER.createObjectNode()
                        .put("max_new_tokens", 128)
                        .put("return_full_text", false))
                .toString();

        // Try task endpoint first, then legacy models endpoint as a fallback
        String[] urls = new String[] {
                "https://router.huggingface.co/hf-inference/text2text?model=" + model,
                "https://router.huggingface.co/hf-inference/models/" + model
        };

        for (String url : urls) {
            try {
                String out = callRouter(url, payload);
                if (out != null && !out.isBlank()) return out.trim();
            } catch (Exception e) {
                System.err.println("[AI] HF call failed (" + url + "): " + e.getMessage());
            }
        }
        return localPolish(text);
    }

    private String callRouter(String url, String payload) throws Exception {
        for (int attempt = 1; attempt <= 3; attempt++) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + hfToken)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            int sc = resp.statusCode();

            if (sc >= 200 && sc < 300) {
                return parseHfResponse(resp.body());
            }

            // 202/503: model loading; 429: rate limit → brief backoff
            if (sc == 202 || sc == 503 || sc == 429) {
                Thread.sleep(600L * attempt);
                continue;
            }

            // 404 on this URL: let caller try next URL
            if (sc == 404) {
                System.err.println("[AI] HF router 404 on " + url + " (will try next).");
                return null;
            }

            System.err.println("[AI] HF router status " + sc + ": " + resp.body());
            return null;
        }
        return null;
    }

    /** Accepts common HF shapes: [{"generated_text":...}], [{"summary_text":...}], ["..."], {...}. */
    private String parseHfResponse(String body) {
        try {
            JsonNode node = MAPPER.readTree(body);

            if (node.isArray() && node.size() > 0) {
                JsonNode first = node.get(0);
                if (first.isObject()) {
                    if (first.has("generated_text")) return first.get("generated_text").asText();
                    if (first.has("summary_text")) return first.get("summary_text").asText();
                }
                if (first.isTextual()) return first.asText();
                for (JsonNode n : node) {
                    if (n.has("generated_text")) return n.get("generated_text").asText();
                    if (n.has("summary_text")) return n.get("summary_text").asText();
                }
            }

            if (node.isObject()) {
                if (node.has("generated_text")) return node.get("generated_text").asText();
                if (node.has("summary_text")) return node.get("summary_text").asText();
                if (node.has("error")) System.err.println("[AI] HF error: " + node.get("error").asText());
            }
        } catch (Exception e) {
            System.err.println("[AI] Failed to parse HF response: " + e.getMessage());
        }
        return null;
    }

    // Conservative fallback that never makes text worse
    private String localPolish(String s) {
        if (s == null) return "";
        String t = s.replaceAll("\\s+", " ").trim();
        t = t.replaceAll("([A-Za-z])\\1{2,}", "$1"); // collapse 3+ repeats
        t = t.replaceAll("!{2,}", "!").replaceAll("\\?{2,}", "?");
        t = t.replaceAll("([!?])\\.", "$1");
        if (!t.isEmpty() && Character.isLetter(t.charAt(0)) && Character.isLowerCase(t.charAt(0))) {
            t = Character.toUpperCase(t.charAt(0)) + t.substring(1);
        }
        if (!t.isEmpty() && !t.matches(".*[.!?]$")) t = t + ".";
        return t;
    }
}
