package com.resumeanalyzer.resumeanalyzer.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeAnalyzerService {

    String process(MultipartFile file, String jobDescription);
}
