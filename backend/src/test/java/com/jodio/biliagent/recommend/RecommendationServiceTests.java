package com.jodio.biliagent.recommend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RecommendationServiceTests {

    @Test
    void shouldNormalizeKnownFeedbackLabels() throws Exception {
        assertThat(invokeNormalize("\u559c\u6b22")).isEqualTo("POSITIVE");
        assertThat(invokeNormalize("\u4e0d\u559c\u6b22")).isEqualTo("NEGATIVE");
        assertThat(invokeNormalize("\u4e00\u822c")).isEqualTo("NEUTRAL");
    }

    @Test
    void shouldFallbackUnknownFeedbackLabelToNeutral() throws Exception {
        assertThat(invokeNormalize("\u672a\u5b9a\u4e49\u6807\u7b7e")).isEqualTo("NEUTRAL");
        assertThat(invokeNormalize("   ")).isEqualTo("NEUTRAL");
        assertThat(invokeNormalize(null)).isEqualTo("NEUTRAL");
    }

    @Test
    void shouldGenerateThreeNonEmptyReasons() throws Exception {
        List<String> reasons = invokeGenerateReasons();

        assertThat(reasons).hasSize(3);
        assertThat(reasons).allSatisfy(reason -> assertThat(reason).isNotBlank());
    }

    @Test
    void shouldReturnNonEmptyProfileSummary() throws Exception {
        String summary = invokeSummarize(List.of("POSITIVE", "NEUTRAL"));

        assertThat(summary).isNotBlank();
    }

    @Test
    void shouldDocumentRateLimitKeyConventionForFutureImplementation() {
        String keyExample = "biliagent:recommend:rate-limit:user:{userId}:daily";

        assertThat(keyExample).contains("recommend:rate-limit");
    }

    private String invokeNormalize(String label) throws Exception {
        Object service = Class.forName("com.jodio.biliagent.feedback.service.FeedbackService")
            .getConstructor()
            .newInstance();
        return (String) service.getClass().getMethod("normalize", String.class).invoke(service, label);
    }

    @SuppressWarnings("unchecked")
    private List<String> invokeGenerateReasons() throws Exception {
        Object service = Class.forName("com.jodio.biliagent.recommend.service.RecommendationService")
            .getConstructor()
            .newInstance();
        return (List<String>) service.getClass().getMethod("generateReasons").invoke(service);
    }

    private String invokeSummarize(List<String> signals) throws Exception {
        Object service = Class.forName("com.jodio.biliagent.profile.service.ProfileService")
            .getConstructor()
            .newInstance();
        return (String) service.getClass().getMethod("summarize", List.class).invoke(service, signals);
    }
}
