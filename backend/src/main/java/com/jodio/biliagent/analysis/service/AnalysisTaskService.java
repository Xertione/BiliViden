package com.jodio.biliagent.analysis.service;

import com.jodio.biliagent.analysis.domain.AnalysisStatus;
import org.springframework.stereotype.Service;

@Service
public class AnalysisTaskService {

    public AnalysisStatus initialStatus() {
        return AnalysisStatus.PENDING;
    }
}
