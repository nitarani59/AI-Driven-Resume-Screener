package com.resumeanalyzer.resumeanalyzer.service.implementation;

import com.resumeanalyzer.resumeanalyzer.service.AgentAIService;
import com.resumeanalyzer.resumeanalyzer.service.ResumeAnalyzerService;
import com.resumeanalyzer.resumeanalyzer.util.ResumeExtractorUtility;
import com.resumeanalyzer.resumeanalyzer.util.ZipExtractorUtility;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeAnalyzerServiceImplementation implements ResumeAnalyzerService {
    @Autowired
    AgentAIService agentAIService;

    @Override
    public List<Map<String, Object>> process(MultipartFile file, String jobDescription) {
        List<File> files = null;
        try {
            files = ZipExtractorUtility.zipExtractorUtility(file.getInputStream(), "/temp/resumes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> texts = ResumeExtractorUtility.extractResults(files);
        System.out.println("texts " + texts);
        return agentAIService.handleBulkResume(jobDescription, texts);
    }
}
