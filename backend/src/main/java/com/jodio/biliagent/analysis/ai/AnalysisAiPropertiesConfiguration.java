package com.jodio.biliagent.analysis.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class AnalysisAiPropertiesConfiguration {
}
