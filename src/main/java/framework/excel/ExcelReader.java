package framework.excel;

import framework.exceptions.FrameworkException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from Excel files.
 * 
 * Supports:
 * - Multiple sheets
 * - Data rows with headers
 * - Various data types (String, numeric, boolean)
 * - Row filtering
 * 
 * Thread-safe: Each instance manages independent Excel files.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class ExcelReader {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
    private final String filePath;
    private Workbook workbook;

    /**
     * Constructs an ExcelReader for the specified file.
     *
     * @param filePath the path to the Excel file
     * @throws FrameworkException if file cannot be opened
     */
    public ExcelReader(String filePath) {
        this.filePath = filePath;
        loadWorkbook();
    }

    /**
     * Loads the Excel workbook from file.
     *
     * @throws FrameworkException if file cannot be loaded
     */
    private void loadWorkbook() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FrameworkException("Excel file not found: " + filePath);
            }
            
            FileInputStream fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);
            logger.info("Excel workbook loaded: {}", filePath);
            
        } catch (IOException e) {
            logger.error("Failed to load Excel file: {}", filePath, e);
            throw new FrameworkException("Failed to load Excel file: " + filePath, e);
        }
    }

    /**
     * Gets all sheet names in the workbook.
     *
     * @return list of sheet names
     */
    public List<String> getAllSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }

    /**
     * Reads all data from a sheet as a list of maps (header-based).
     * First row is treated as headers.
     *
     * @param sheetName the sheet name to read
     * @return list of maps where each map represents a row
     */
    public List<Map<String, String>> readSheet(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.warn("Sheet not found: {}", sheetName);
                return data;
            }
            
            // Get headers from first row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                logger.warn("No header row found in sheet: {}", sheetName);
                return data;
            }
            
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(getCellValue(cell));
            }
            
            // Read data rows
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    String value = getCellValue(cell);
                    rowData.put(headers.get(i), value);
                }
                
                data.add(rowData);
            }
            
            logger.info("Read {} rows from sheet: {}", data.size(), sheetName);
            
        } catch (Exception e) {
            logger.error("Failed to read sheet: {}", sheetName, e);
            throw new FrameworkException("Failed to read sheet: " + sheetName, e);
        }
        
        return data;
    }

    /**
     * Gets a specific row as a map from a sheet.
     *
     * @param sheetName the sheet name
     * @param rowNumber the row number (0-based, 0 is headers)
     * @return map representing the row
     */
    public Map<String, String> getRow(String sheetName, int rowNumber) {
        List<Map<String, String>> allData = readSheet(sheetName);
        if (rowNumber >= 0 && rowNumber < allData.size()) {
            return allData.get(rowNumber);
        }
        return new HashMap<>();
    }

    /**
     * Gets the value of a specific cell.
     *
     * @param sheetName the sheet name
     * @param row the row number (0-based)
     * @param column the column number (0-based)
     * @return the cell value as string
     */
    public String getCellValue(String sheetName, int row, int column) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            Row xlRow = sheet.getRow(row);
            Cell cell = xlRow.getCell(column);
            return getCellValue(cell);
            
        } catch (Exception e) {
            logger.error("Failed to get cell value: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Gets the number of rows in a sheet (excluding header).
     *
     * @param sheetName the sheet name
     * @return the number of data rows
     */
    public int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return 0;
        }
        return sheet.getLastRowNum(); // Excludes header
    }

    /**
     * Converts a Cell value to String.
     *
     * @param cell the Excel cell
     * @return the cell value as string
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        CellType cellType = cell.getCellType();
        
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double numValue = cell.getNumericCellValue();
                // Check if it's a whole number
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }

    /**
     * Closes the workbook and releases resources.
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Workbook closed: {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to close workbook", e);
        }
    }
}
