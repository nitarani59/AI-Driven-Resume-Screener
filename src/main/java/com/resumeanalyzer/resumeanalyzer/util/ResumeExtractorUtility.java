package com.resumeanalyzer.resumeanalyzer.util;


import org.ccil.cowan.tagsoup.AutoDetector;

import java.io.File;
import java.io.FileInputStream;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;

import java.io.*;import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ResumeExtractorUtility {
//    private static final Tika tika = new Tika();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static List<String> extractResults(List<File> files) {
        System.out.println("files " + files);

        List<Future<String>> futures = new ArrayList<>();
        List<String> results = new ArrayList<>();
        for (File file : files) {
            System.out.println("Parsing file: " + file.getAbsolutePath() + ", size: " + file.length());
            futures.add(executorService.submit(() -> extractText(file)));
        }
        for (Future<String> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return results;
    }

    private static String extractText(File file) {
        try (InputStream input = new FileInputStream(file)) {
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1); // unlimited text
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(input, handler, metadata, context);
            return handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
