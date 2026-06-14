package com.jodio.biliagent.analysis.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(OnOpenAiApiKeyPresentCondition.class)
public class AnalysisAiConfiguration {

    @Bean
    ChatModel analysisChatModel(OpenAiProperties properties) {
        return OpenAiChatModel.builder()
            .baseUrl(properties.getBaseUrl())
            .apiKey(properties.getApiKey())
            .modelName(properties.getModel())
            .build();
    }

    @Bean
    VideoAnalysisAiService videoAnalysisAiService(ChatModel analysisChatModel) {
        return new LangChain4jVideoAnalysisAiService(analysisChatModel);
    }
}
