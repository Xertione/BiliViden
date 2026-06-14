package com.jodio.biliagent.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jodio.biliagent.analysis.ai.VideoAnalysisPromptCatalog;
import com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto;
import com.jodio.biliagent.analysis.service.VideoAnalysisExecutor;
import java.util.List;
import org.junit.jupiter.api.Test;

class VideoAnalysisExecutorTests {

    private final VideoAnalysisExecutor executor = new VideoAnalysisExecutor(material -> {
        throw new UnsupportedOperationException("not used in validation tests");
    });

    @Test
    void shouldRejectEmptySummary() {
        VideoAnalysisResultDto dto = new VideoAnalysisResultDto(
            "",
            List.of("point"),
            List.of("kw"),
            List.of("c"),
            "ok",
            List.of("title")
        );

        assertThatThrownBy(() -> executor.validate(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("summary must not be blank");
    }

    @Test
    void shouldRejectEmptyCorePoints() {
        VideoAnalysisResultDto dto = new VideoAnalysisResultDto(
            "summary",
            List.of(),
            List.of("kw"),
            List.of("c"),
            "ok",
            List.of("title")
        );

        assertThatThrownBy(() -> executor.validate(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("corePoints must not be empty");
    }

    @Test
    void shouldAcceptValidResult() {
        VideoAnalysisResultDto dto = new VideoAnalysisResultDto(
            "summary",
            List.of("point"),
            List.of("kw"),
            List.of("c"),
            "ok",
            List.of("title")
        );

        assertThatCode(() -> executor.validate(dto)).doesNotThrowAnyException();
    }

    @Test
    void shouldExposeStablePromptVersions() {
        assertThat(VideoAnalysisPromptCatalog.ANALYSIS_PROMPT_VERSION).isEqualTo("analysis-v1");
        assertThat(VideoAnalysisPromptCatalog.CARD_PROMPT_VERSION).isEqualTo("card-v1");
        assertThat(VideoAnalysisPromptCatalog.QA_PROMPT_VERSION).isEqualTo("qa-v1");
    }
}
