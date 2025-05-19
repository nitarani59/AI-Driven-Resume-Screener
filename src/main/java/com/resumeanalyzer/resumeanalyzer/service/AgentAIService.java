package com.resumeanalyzer.resumeanalyzer.service;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;

import java.util.List;
import java.util.Map;

public interface AgentAIService {

    public List<ResumeResult> handleBulkResume(String jd, List<String> resumeText);

}
