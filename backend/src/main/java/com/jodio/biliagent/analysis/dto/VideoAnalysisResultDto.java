package com.jodio.biliagent.analysis.dto;

import java.util.List;

public record VideoAnalysisResultDto(
    String summary,
    List<String> corePoints,
    List<String> keywords,
    List<String> controversies,
    String attitudeSuggestion,
    List<String> sourceBasis
) {
}
