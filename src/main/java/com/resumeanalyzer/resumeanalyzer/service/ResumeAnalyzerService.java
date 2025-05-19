package com.resumeanalyzer.resumeanalyzer.service;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ResumeAnalyzerService {

    List<ResumeResult> process(MultipartFile file, String jobDescription);

    List<ResumeResult> resumeAnalyzeResponse(String uploadId);

    byte[] downloadResultsAsExcel(String uploadId) throws IOException;
}
