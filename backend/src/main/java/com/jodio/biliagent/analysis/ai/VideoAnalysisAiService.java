package com.jodio.biliagent.analysis.ai;

import com.jodio.biliagent.analysis.dto.VideoAnalysisResultDto;

public interface VideoAnalysisAiService {

    VideoAnalysisResultDto analyze(String material);
}
