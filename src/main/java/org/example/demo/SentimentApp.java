package org.example.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class SentimentApp {

    private final Counter positiveRequests;
    private final Counter negativeRequests;

    public SentimentApp(MeterRegistry meterRegistry) {
        // Эти метрики появятся в Prometheus
        this.positiveRequests = meterRegistry.counter("sentiment_api_requests_total", "type", "positive");
        this.negativeRequests = meterRegistry.counter("sentiment_api_requests_total", "type", "negative");
    }

    public static void main(String[] args) {
        SpringApplication.run(SentimentApp.class, args);
    }

    @GetMapping("/api/sentiment")
    public Map<String, Object> analyzeText(@RequestParam(name = "text", defaultValue = "") String text) {
        String result = detectSentiment(text);

        if ("negative".equals(result)) {
            negativeRequests.increment();
        } else {
            positiveRequests.increment();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sentiment", result);
        response.put("length", text.length());
        response.put("originalText", text);
        return response;
    }

    private String detectSentiment(String input) {
        String normalized = input.toLowerCase().trim();
        return normalized.contains("bad") ? "negative" : "positive";
    }
}
