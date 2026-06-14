package com.jodio.biliagent.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import com.jodio.biliagent.analysis.ai.AnalysisAiConfiguration;
import com.jodio.biliagent.analysis.ai.AnalysisAiPropertiesConfiguration;
import com.jodio.biliagent.analysis.ai.OpenAiProperties;
import com.jodio.biliagent.analysis.ai.VideoAnalysisAiService;
import com.jodio.biliagent.analysis.service.VideoAnalysisExecutor;
import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class VideoAnalysisAiConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            AnalysisAiPropertiesConfiguration.class,
            AnalysisAiConfiguration.class
        ));

    @Test
    void shouldNotCreateAiBeansWhenApiKeyIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(OpenAiProperties.class);
            assertThat(context).doesNotHaveBean(ChatModel.class);
            assertThat(context).doesNotHaveBean(VideoAnalysisAiService.class);
            assertThat(context).doesNotHaveBean(VideoAnalysisExecutor.class);
        });
    }

    @Test
    void shouldNotCreateAiBeansWhenApiKeyIsBlank() {
        contextRunner
            .withPropertyValues(
                "app.ai.openai.base-url=https://api.example.test/v1",
                "app.ai.openai.api-key=   ",
                "app.ai.openai.model=deepseek-chat"
            )
            .run(context -> {
                assertThat(context).doesNotHaveBean(ChatModel.class);
                assertThat(context).doesNotHaveBean(VideoAnalysisAiService.class);
            });
    }

    @Test
    void shouldCreateAiBeansWhenApiKeyExists() {
        contextRunner
            .withPropertyValues(
                "app.ai.openai.base-url=https://api.example.test/v1",
                "app.ai.openai.api-key=dummy-key",
                "app.ai.openai.model=deepseek-chat"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(ChatModel.class);
                assertThat(context).hasSingleBean(VideoAnalysisAiService.class);
            });
    }
}
