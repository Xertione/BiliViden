package com.jodio.biliagent.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.junit.jupiter.api.Test;

class VideoAnalysisExecutorTests {

    @Test
    void shouldRejectEmptySummary() {
        Object executor = newExecutor();
        Object dto = newResultDto("", List.of("point"));

        assertThatThrownBy(() -> validate(executor, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("summary must not be blank");
    }

    @Test
    void shouldRejectEmptyCorePoints() {
        Object executor = newExecutor();
        Object dto = newResultDto("summary", List.of());

        assertThatThrownBy(() -> validate(executor, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("corePoints must not be empty");
    }

    @Test
    void shouldAcceptValidResult() {
        Object executor = newExecutor();
        Object dto = newResultDto("summary", List.of("point"));

        assertThatCode(() -> validate(executor, dto)).doesNotThrowAnyException();
    }

    @Test
    void shouldExposeStablePromptVersions() throws Exception {
        Class<?> promptCatalogClass = Class.forName("com.jodio.biliagent.analysis.ai.VideoAnalysisPromptCatalog");

        assertThat(promptCatalogClass.getField("ANALYSIS_PROMPT_VERSION").get(null)).isEqualTo("analysis-v1");
        assertThat(promptCatalogClass.getField("CARD_PROMPT_VERSION").get(null)).isEqualTo("card-v1");
        assertThat(promptCatalogClass.getField("QA_PROMPT_VERSION").get(null)).isEqualTo("qa-v1");
    }

    private static Object newExecutor() {
        try {
            Class<?> aiServiceClass = Class.forName("com.jodio.biliagent.analysis.ai.VideoAnalysisAiService");
            Class<?> executorClass = Class.forName("com.jodio.biliagent.analysis.service.VideoAnalysisExecutor");
            Object aiService = Proxy.newProxyInstance(
                aiServiceClass.getClassLoader(),
                new Class<?>[] { aiServiceClass },
                (proxy, method, args) -> {
                    throw new UnsupportedOperationException("not used in validation tests");
                }
            );
            return executorClass.getConstructor(aiServiceClass).newInstance(aiService);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("failed to create executor", exception);
        }
    }

    private static Object newResultDto(String summary, List<String> corePoints) {
        try {
            Class<?> dtoClass = Class.forName("com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto");
            return dtoClass.getConstructor(
                    String.class,
                    List.class,
                    List.class,
                    List.class,
                    String.class,
                    List.class
                )
                .newInstance(summary, corePoints, List.of("kw"), List.of("c"), "ok", List.of("title"));
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("failed to create dto", exception);
        }
    }

    private static void validate(Object executor, Object dto) {
        try {
            Class<?> dtoClass = Class.forName("com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto");
            Method validateMethod = executor.getClass().getMethod("validate", dtoClass);
            validateMethod.invoke(executor, dto);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new AssertionError("unexpected checked exception from validate", cause);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("failed to invoke validate", exception);
        }
    }
}
