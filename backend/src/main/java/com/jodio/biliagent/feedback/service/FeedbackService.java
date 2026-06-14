package com.jodio.biliagent.feedback.service;

import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    public String normalize(String label) {
        if (label == null || label.isBlank()) {
            return "NEUTRAL";
        }

        String normalizedLabel = label.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedLabel) {
            case "\u559c\u6b22", "\u60f3\u770b\u66f4\u591a", "\u6709\u5e2e\u52a9" -> "POSITIVE";
            case "\u4e0d\u559c\u6b22", "\u4e0d\u60f3\u770b", "\u6ca1\u5e2e\u52a9" -> "NEGATIVE";
            case "\u8df3\u8fc7", "\u4e00\u822c", "\u65e0\u611f" -> "NEUTRAL";
            default -> "NEUTRAL";
        };
    }
}
