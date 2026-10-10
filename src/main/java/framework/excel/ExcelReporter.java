package framework.excel;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.reporting.Reporter;
import framework.reporting.TestExecutionSummary;
import framework.reporting.TestResult;

/**
 * Concrete Excel reporter implementation for test execution results.
 */
public class ExcelReporter implements Reporter {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReporter.class);
    private final String outputDirectory;
    private String lastReportPath;

    public ExcelReporter(String outputDirectory) {
        this.outputDirectory = outputDirectory == null || outputDirectory.trim().isEmpty() ? "reports/excel" : outputDirectory.trim();
        File dir = new File(this.outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void generate(TestExecutionSummary summary, List<TestResult> results) {
        ExcelWorkbookBuilder builder = new ExcelWorkbookBuilder();

        List<String[]> resultRows = new ArrayList<>();
        List<String[]> failedRows = new ArrayList<>();

        for (TestResult result : results) {
            String[] row = new String[]{
                result.getTestId() == null ? "" : result.getTestId(),
                result.getScreenId() == null ? "" : result.getScreenId(),
                result.getScreenName() == null ? "" : result.getScreenName(),
                result.getTestName() == null ? "" : result.getTestName(),
                result.getStatus() == null ? "" : result.getStatus().name(),
                result.getStartTime() == null ? "" : result.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                result.getEndTime() == null ? "" : result.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                String.valueOf(result.getDuration()),
                result.getErrorMessage() == null ? "" : result.getErrorMessage(),
                result.getExceptionType() == null ? "" : result.getExceptionType(),
                result.getScreenshotPath() == null ? "" : result.getScreenshotPath()
            };
            resultRows.add(row);

            if (result.getStatus() == framework.reporting.TestStatus.FAIL) {
                failedRows.add(new String[]{
                    result.getTestId() == null ? "" : result.getTestId(),
                    result.getTestName() == null ? "" : result.getTestName(),
                    result.getScreenName() == null ? "" : result.getScreenName(),
                    result.getStatus() == null ? "" : result.getStatus().name(),
                    result.getErrorMessage() == null ? "" : result.getErrorMessage(),
                    result.getExceptionType() == null ? "" : result.getExceptionType(),
                    result.getScreenshotPath() == null ? "" : result.getScreenshotPath()
                });
            }
        }

        // Create the detailed results sheet first so Excel opens on the test rows
        // when the workbook is generated from the interactive Main runner.
        builder.addResultsSheet(resultRows);
        builder.addSummarySheet(
                summary.getStartTime() == null ? "" : summary.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                summary.getStartTime() == null ? "" : summary.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                summary.getEndTime() == null ? "" : summary.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                summary.getTotal(),
                summary.getPassed(),
                summary.getFailed(),
                summary.getSkipped(),
                summary.getTotalDuration()
        );
        builder.addFailedTestsSheet(failedRows);

        String filePath = builder.save(outputDirectory);
        lastReportPath = filePath;
        logger.info("Excel report generated: {}", filePath);
    }

    public String getLastReportPath() {
        return lastReportPath;
    }
}
