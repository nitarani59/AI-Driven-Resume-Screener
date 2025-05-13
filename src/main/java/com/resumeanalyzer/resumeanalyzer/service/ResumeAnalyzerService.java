package com.resumeanalyzer.resumeanalyzer.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ResumeAnalyzerService {

    List<Map<String, Object>> process(MultipartFile file, String jobDescription);
}
