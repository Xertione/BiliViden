package com.jodio.biliagent.analysis.ai;

import com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;

public class LangChain4jVideoAnalysisAiService implements VideoAnalysisAiService {

    private final ChatModel chatModel;

    public LangChain4jVideoAnalysisAiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public VideoAnalysisResultDto analyze(String material) {
        throw new UnsupportedOperationException("Task 6 skeleton does not execute real model calls yet");
    }

    public SystemMessage systemMessage() {
        return SystemMessage.from(
            "你是一个结构化视频分析助手，必须输出摘要、核心观点、关键词、争议点、态度建议、分析依据。"
        );
    }

    public UserMessage userMessage(String material) {
        return UserMessage.from("请分析以下视频素材：" + material);
    }

    public ChatModel chatModel() {
        return chatModel;
    }
}
