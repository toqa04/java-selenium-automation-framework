package framework.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.exceptions.FrameworkException;

/**
 * Writes test data and results to Excel files.
 *
 * Supports: - Creating new workbooks - Adding sheets - Writing headers and data
 * rows - Writing test results
 *
 * Thread-safe: Each instance manages independent Excel files.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class ExcelWriter {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWriter.class);
    private final String filePath;
    private final XSSFWorkbook workbook;

    /**
     * Constructs an ExcelWriter for the specified file path.
     *
     * @param filePath the path where the Excel file will be saved
     */
    public ExcelWriter(String filePath) {
        this.filePath = filePath;
        this.workbook = new XSSFWorkbook();
        logger.info("ExcelWriter created for: {}", filePath);
    }

    /**
     * Creates a new sheet in the workbook.
     *
     * @param sheetName the name of the new sheet
     */
    public void createSheet(String sheetName) {
        try {
            if (workbook.getSheet(sheetName) != null) {
                logger.warn("Sheet already exists: {}", sheetName);
                return;
            }

            workbook.createSheet(sheetName);
            logger.info("Sheet created: {}", sheetName);

        } catch (Exception e) {
            logger.error("Failed to create sheet: {}", sheetName, e);
            throw new FrameworkException("Failed to create sheet: " + sheetName, e);
        }
    }

    /**
     * Writes header row to a sheet.
     *
     * @param sheetName the sheet name
     * @param headers the column headers
     */
    public void writeHeaders(String sheetName, String[] headers) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                createSheet(sheetName);
                sheet = workbook.getSheet(sheetName);
            }

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            logger.debug("Headers written to sheet: {}", sheetName);

        } catch (Exception e) {
            logger.error("Failed to write headers to sheet: {}", sheetName, e);
            throw new FrameworkException("Failed to write headers", e);
        }
    }

    /**
     * Writes a data row to a sheet.
     *
     * @param sheetName the sheet name
     * @param rowData the row data as a map (key = header, value = data)
     * @param headers the column headers in order
     */
    public void writeRow(String sheetName, Map<String, String> rowData, String[] headers) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet not found: {}", sheetName);
                return;
            }

            int rowNumber = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowNumber);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                String value = rowData.getOrDefault(headers[i], "");
                cell.setCellValue(value);
            }

            logger.debug("Row {} written to sheet: {}", rowNumber, sheetName);

        } catch (Exception e) {
            logger.error("Failed to write row to sheet: {}", sheetName, e);
            throw new FrameworkException("Failed to write row", e);
        }
    }

    /**
     * Writes multiple rows to a sheet.
     *
     * @param sheetName the sheet name
     * @param rows the list of rows to write (each row is a map)
     * @param headers the column headers in order
     */
    public void writeRows(String sheetName, List<Map<String, String>> rows, String[] headers) {
        for (Map<String, String> row : rows) {
            writeRow(sheetName, row, headers);
        }
        logger.info("Wrote {} rows to sheet: {}", rows.size(), sheetName);
    }

    /**
     * Writes a value to a specific cell.
     *
     * @param sheetName the sheet name
     * @param row the row number (0-based)
     * @param column the column number (0-based)
     * @param value the cell value
     */
    public void setCellValue(String sheetName, int row, int column, String value) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet not found: {}", sheetName);
                return;
            }

            Row xlRow = sheet.getRow(row);
            if (xlRow == null) {
                xlRow = sheet.createRow(row);
            }

            Cell cell = xlRow.createCell(column);
            cell.setCellValue(value);

        } catch (Exception e) {
            logger.error("Failed to set cell value", e);
            throw new FrameworkException("Failed to set cell value", e);
        }
    }

    /**
     * Saves the workbook to file.
     *
     * @throws FrameworkException if save fails
     */
    public void save() {
        try {
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            logger.info("Excel file saved: {}", filePath);

        } catch (IOException e) {
            logger.error("Failed to save Excel file: {}", filePath, e);
            throw new FrameworkException("Failed to save Excel file: " + filePath, e);
        }
    }

    /**
     * Closes the workbook and releases resources.
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Workbook closed");
            }
        } catch (IOException e) {
            logger.error("Failed to close workbook", e);
        }
    }
}
