package com.resumeanalyzer.resumeanalyzer.repository;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResumeRepository extends MongoRepository<ResumeResult, String> {
    List<ResumeResult> findAllByUploadId(String uploadId);

}
