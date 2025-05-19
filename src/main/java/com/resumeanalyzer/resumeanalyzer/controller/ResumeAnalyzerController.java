package com.resumeanalyzer.resumeanalyzer.controller;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;
import com.resumeanalyzer.resumeanalyzer.service.ResumeAnalyzerService;
import com.resumeanalyzer.resumeanalyzer.util.ExcelExportUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResumeAnalyzerController {

    @Autowired
    ResumeAnalyzerService resumeAnalyzerService;
    @PostMapping("/zip/upload")
    public ResponseEntity<List<ResumeResult>> uploadResumes(@RequestParam MultipartFile file, @RequestParam String jobDescription) {
        return ResponseEntity.ok(resumeAnalyzerService.process(file, jobDescription));
    }

    @GetMapping("/download-results/{uploadId}")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable String uploadId) throws IOException {
        // 1. Fetch all resume results with the given uploadId
        byte[] results = resumeAnalyzerService.downloadResultsAsExcel(uploadId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume_results_" + uploadId + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(results);
    }
}
