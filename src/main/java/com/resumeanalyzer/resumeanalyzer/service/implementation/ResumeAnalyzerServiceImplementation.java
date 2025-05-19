package com.resumeanalyzer.resumeanalyzer.service.implementation;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;
import com.resumeanalyzer.resumeanalyzer.repository.ResumeRepository;
import com.resumeanalyzer.resumeanalyzer.service.AgentAIService;
import com.resumeanalyzer.resumeanalyzer.service.ResumeAnalyzerService;
import com.resumeanalyzer.resumeanalyzer.util.ExcelExportUtility;
import com.resumeanalyzer.resumeanalyzer.util.ResumeExtractorUtility;
import com.resumeanalyzer.resumeanalyzer.util.ZipExtractorUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ResumeAnalyzerServiceImplementation implements ResumeAnalyzerService {
    @Autowired
    AgentAIService agentAIService;
    @Autowired
    ResumeRepository resumeRepository;
    static List<ResumeResult> response = new ArrayList<>();

    @Override
    public List<ResumeResult> process(MultipartFile file, String jobDescription) {
        List<File> files = null;
        try {
            files = ZipExtractorUtility.zipExtractorUtility(file.getInputStream(), "/temp/resumes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> texts = ResumeExtractorUtility.extractResults(files);
        List<ResumeResult> mappedResults = (agentAIService.handleBulkResume(jobDescription, texts));
        System.out.println("mappee " + mappedResults);
        return resumeRepository.saveAll(mappedResults);
    }

    @Override
    public List<ResumeResult> resumeAnalyzeResponse(String uploadId) {
        return resumeRepository.findAllByUploadId(uploadId);
    }

    public byte[] downloadResultsAsExcel(String uploadId) throws IOException {
        List<ResumeResult> results = resumeAnalyzeResponse(uploadId);
        return ExcelExportUtility.generateExcelResults(results); // returns byte[]
    }
}
