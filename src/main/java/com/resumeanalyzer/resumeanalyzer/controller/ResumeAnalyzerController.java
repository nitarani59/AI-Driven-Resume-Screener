package com.resumeanalyzer.resumeanalyzer.controller;

import com.resumeanalyzer.resumeanalyzer.service.ResumeAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ResumeAnalyzerController {

    @Autowired
    ResumeAnalyzerService resumeAnalyzerService;
    @PostMapping("/zip/upload")
    public ResponseEntity<String> uploadResumes(@RequestParam MultipartFile file, @RequestParam String jobDescription) {
        return ResponseEntity.ok(resumeAnalyzerService.process(file, jobDescription));
    }
}
