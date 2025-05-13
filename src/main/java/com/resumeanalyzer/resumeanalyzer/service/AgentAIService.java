package com.resumeanalyzer.resumeanalyzer.service;

import java.util.List;
import java.util.Map;

public interface AgentAIService {

    public List<Map<String, Object>> handleBulkResume(String jd, List<String> resumeText);

}
