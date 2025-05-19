package com.resumeanalyzer.resumeanalyzer.util;

import com.resumeanalyzer.resumeanalyzer.model.ResumeResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportUtility {

    public static byte[] generateExcelResults(List<ResumeResult> results) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resume Results");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Candidate Name", "Email", "Mobile", "Experience (Years)",
                "Previous Companies", "Highest Education", "Education Year",
                "Matched Skills", "Soft Skills", "Score", "Extra Notes", "Upload ID"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fill data rows
        int rowIdx = 1;
        for (ResumeResult r : results) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getName());
            row.createCell(1).setCellValue(r.getEmailId());
            row.createCell(2).setCellValue(r.getMobileNumber());
            row.createCell(3).setCellValue(r.getExperienceYears());

            row.createCell(4).setCellValue(String.join(", ", r.getPreviousCompanies()));
            row.createCell(5).setCellValue(r.getHighestEducation());
            row.createCell(6).setCellValue(r.getEducationYear());

            row.createCell(7).setCellValue(String.join(", ", r.getMatchedSkills()));
            row.createCell(8).setCellValue(String.join(", ", r.getSoftSkills()));
            row.createCell(9).setCellValue(r.getScore());
            row.createCell(10).setCellValue(r.getExtraNotes());
            row.createCell(11).setCellValue(r.getUploadId());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert workbook to byte[]
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
