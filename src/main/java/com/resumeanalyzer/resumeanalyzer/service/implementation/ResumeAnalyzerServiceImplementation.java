package com.resumeanalyzer.resumeanalyzer.service.implementation;

import com.resumeanalyzer.resumeanalyzer.service.ResumeAnalyzerService;
import com.resumeanalyzer.resumeanalyzer.util.ZipExtractorUtility;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ResumeAnalyzerServiceImplementation implements ResumeAnalyzerService {
    @Override
    public String process(MultipartFile file, String jobDescription) {
        try {
            List<File> files = ZipExtractorUtility.zipExtractorUtility(file.getInputStream(), "/temp/resumes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
