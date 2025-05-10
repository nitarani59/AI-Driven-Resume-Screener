package com.resumeanalyzer.resumeanalyzer.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractorUtility {

    public static List<File> zipExtractorUtility(InputStream inputStream, String destination) {
        List<File> extractedFiles = new ArrayList<>();
        try(ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File outFile = new File(destination, zipEntry.getName());
                outFile.getParentFile().mkdirs();

                try(FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
                    byte[] bufferSize = new byte[1024];
                    int len;
                    while((len = zipInputStream.read(bufferSize)) > 0) {
                        fileOutputStream.write(len);
                    }
                }
            extractedFiles.add(outFile);
            }
            zipInputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extractedFiles;
    }
}
