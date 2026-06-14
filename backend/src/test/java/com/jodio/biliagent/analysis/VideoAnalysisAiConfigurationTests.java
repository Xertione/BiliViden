package com.jodio.biliagent.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.chat.ChatModel;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class VideoAnalysisAiConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(configurationClasses()));

    @Test
    void shouldNotCreateAiBeansWhenApiKeyIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(loadClass("com.jodio.biliagent.analysis.ai.OpenAiProperties"));
            assertThat(context).doesNotHaveBean(ChatModel.class);
            assertThat(context).doesNotHaveBean(loadClass("com.jodio.biliagent.analysis.ai.VideoAnalysisAiService"));
            assertThat(context).doesNotHaveBean(loadClass("com.jodio.biliagent.analysis.service.VideoAnalysisExecutor"));
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
                assertThat(context).doesNotHaveBean(loadClass("com.jodio.biliagent.analysis.ai.VideoAnalysisAiService"));
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
                assertThat(context).hasSingleBean(loadClass("com.jodio.biliagent.analysis.ai.VideoAnalysisAiService"));
            });
    }

    private static Class<?>[] configurationClasses() {
        return Stream.of(
                "com.jodio.biliagent.analysis.ai.AnalysisAiPropertiesConfiguration",
                "com.jodio.biliagent.analysis.ai.AnalysisAiConfiguration"
            )
            .map(VideoAnalysisAiConfigurationTests::loadClass)
            .toArray(Class<?>[]::new);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new AssertionError("missing class: " + className, exception);
        }
    }
}
