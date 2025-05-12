package com.resumeanalyzer.resumeanalyzer.util;

import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResumeExtractorUtility {
    private static final Tika tika = new Tika();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @SneakyThrows
    public static List<String> extractResults(List<File> files) {
        List<Future<String>> futures = new ArrayList<>();
        List<String> results = new ArrayList<>();
        for (File file : files) {
            futures.add(executorService.submit(() -> extractText(file)));
        }
        for (Future<String> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    private static String extractText(File file) {
        String text = "";
        try {
            text = tika.parseToString(file);
        } catch (IOException | TikaException ex) {
            ex.printStackTrace();
        }
        return text;
    }
}
