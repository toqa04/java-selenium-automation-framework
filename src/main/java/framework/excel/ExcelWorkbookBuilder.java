package framework.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for an Excel workbook with summary and result sheets.
 */
public class ExcelWorkbookBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWorkbookBuilder.class);
    private final XSSFWorkbook workbook;
    private final ExcelStyleManager styleManager;

    public ExcelWorkbookBuilder() {
        this.workbook = new XSSFWorkbook();
        this.styleManager = new ExcelStyleManager();
    }

    public Sheet createSheet(String name) {
        return workbook.createSheet(name);
    }

    public void addSummarySheet(String executionDate, String startTime, String endTime, int total, int passed, int failed, int skipped, long durationMs) {
        Sheet sheet = workbook.createSheet("Execution Summary");
        sheet.createFreezePane(0, 1);
        styleManager.createDefaultStyle(workbook);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Execution Summary");
        CellStyle titleStyle = styleManager.createHeaderStyle(workbook);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        String[] labels = {"Execution Date", "Start Time", "End Time", "Total", "Passed", "Failed", "Skipped", "Duration"};
        String[] values = {
            executionDate,
            startTime,
            endTime,
            String.valueOf(total),
            String.valueOf(passed),
            String.valueOf(failed),
            String.valueOf(skipped),
            String.valueOf(durationMs) + " ms"
        };

        for (int i = 0; i < labels.length; i++) {
            Row row = sheet.createRow(i + 1);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(labels[i] + ": " + values[i]);
            labelCell.setCellStyle(styleManager.createHeaderStyle(workbook));

            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(values[i]);
            valueCell.setCellStyle(styleManager.createDefaultStyle(workbook));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.setColumnWidth(0, 26000);
        sheet.setColumnWidth(1, 22000);
    }

    public void addResultsSheet(List<String[]> rows) {
        Sheet sheet = workbook.createSheet("Test");
        sheet.createFreezePane(0, 1);

        String[] headers = {"Test ID", "Screen ID", "Screen", "Test Case", "Status", "Start Time", "End Time", "Duration (ms)", "Failure Reason", "Exception Type", "Screenshot"};
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = styleManager.createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            Row row = sheet.createRow(rowNum + 1);
            String[] values = rows.get(rowNum);
            for (int col = 0; col < values.length; col++) {
                Cell cell = row.createCell(col);
                cell.setCellValue(values[col] == null ? "" : values[col]);
                cell.setCellStyle(styleManager.createDefaultStyle(workbook));
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            int width = Math.min(50000, sheet.getColumnWidth(i) + 500);
            sheet.setColumnWidth(i, width);
        }

        if (!rows.isEmpty()) {
            sheet.setAutoFilter(new CellRangeAddress(0, rows.size(), 0, headers.length - 1));
        }
    }

    public void addFailedTestsSheet(List<String[]> rows) {
        Sheet sheet = workbook.createSheet("Failed Tests");
        sheet.createFreezePane(0, 1);

        String[] headers = {"Test ID", "Test Case", "Screen", "Status", "Failure Reason", "Exception Type", "Screenshot"};
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = styleManager.createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            Row row = sheet.createRow(rowNum + 1);
            String[] values = rows.get(rowNum);
            for (int col = 0; col < values.length; col++) {
                Cell cell = row.createCell(col);
                cell.setCellValue(values[col] == null ? "" : values[col]);
                cell.setCellStyle(styleManager.createDefaultStyle(workbook));
            }
        }
    }

    public String save(String targetDirectory) {
        File tempFile = null;
        try {
            File dir = new File(targetDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String filePath = new File(dir, "Test_" + timestamp + ".xlsx").getAbsolutePath();
            tempFile = new File(filePath + ".part");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                workbook.write(out);
            } finally {
                workbook.close();
            }
            if (!tempFile.renameTo(new File(filePath))) {
                throw new IOException("Could not finalize Excel report: " + filePath);
            }
            logger.info("Excel workbook saved to: {}", filePath);
            return filePath;
        } catch (IOException | RuntimeException e) {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            logger.error("Failed to write Excel workbook.", e);
            throw new RuntimeException("Failed to write Excel workbook", e);
        }
    }
}
