package com.resumeanalyzer.resumeanalyzer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;

import java.util.List;
import java.util.Map;

public class ResumeMapperUtil {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static List<ResumeResult> mapList(List<Map<String, Object>> records) {
       return records.stream().map(
                record -> objectMapper.convertValue(record, ResumeResult.class)).toList();
    }
}
