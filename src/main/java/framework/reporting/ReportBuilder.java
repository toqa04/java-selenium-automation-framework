package framework.reporting;

import framework.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates HTML and text-based test reports.
 * 
 * Creates formatted reports of test execution including:
 * - Test summary (passed, failed, skipped)
 * - Test details
 * - Execution time
 * - Environment information
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class ReportBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportBuilder.class);
    private final String reportDirectory;
    private final String executionStartTime;
    private final List<TestReport> testReports = new ArrayList<>();

    /**
     * Constructs a ReportBuilder for the specified directory.
     *
     * @param reportDirectory the directory to save reports
     */
    public ReportBuilder(String reportDirectory) {
        this.reportDirectory = reportDirectory;
        this.executionStartTime = DateUtils.getCurrentDateTimeAsString();
        
        // Create directory if it doesn't exist
        File dir = new File(reportDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Adds a test report to the suite.
     *
     * @param testReport the test report to add
     */
    public void addTestReport(TestReport testReport) {
        testReports.add(testReport);
    }

    /**
     * Generates an HTML report of all test executions.
     *
     * @return the file path of the generated report
     */
    public String generateHtmlReport() {
        try {
            String fileName = "report_" + DateUtils.getTimestampForFileName() + ".html";
            String filePath = new File(reportDirectory, fileName).getAbsolutePath();
            
            String htmlContent = buildHtmlContent();
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(htmlContent);
            }
            
            logger.info("HTML report generated: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to generate HTML report", e);
            return null;
        }
    }

    /**
     * Generates a text report of all test executions.
     *
     * @return the file path of the generated report
     */
    public String generateTextReport() {
        try {
            String fileName = "report_" + DateUtils.getTimestampForFileName() + ".txt";
            String filePath = new File(reportDirectory, fileName).getAbsolutePath();
            
            String textContent = buildTextContent();
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(textContent);
            }
            
            logger.info("Text report generated: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to generate text report", e);
            return null;
        }
    }

    /**
     * Builds HTML content for the report.
     *
     * @return the HTML string
     */
    private String buildHtmlContent() {
        long totalTests = testReports.size();
        long passedTests = testReports.stream().filter(r -> r.getStatus().equals("PASSED")).count();
        long failedTests = testReports.stream().filter(r -> r.getStatus().equals("FAILED")).count();
        long skippedTests = totalTests - passedTests - failedTests;
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Test Execution Report</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("h1 { color: #333; }\n");
        html.append(".summary { background-color: #f5f5f5; padding: 10px; border-radius: 5px; margin: 10px 0; }\n");
        html.append(".passed { color: green; }\n");
        html.append(".failed { color: red; }\n");
        html.append(".skipped { color: orange; }\n");
        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #4CAF50; color: white; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<h1>Test Execution Report</h1>\n");
        html.append("<p>Execution Start: ").append(executionStartTime).append("</p>\n");
        
        html.append("<div class=\"summary\">\n");
        html.append("<h2>Summary</h2>\n");
        html.append("<p><strong>Total Tests:</strong> ").append(totalTests).append("</p>\n");
        html.append("<p class=\"passed\"><strong>Passed:</strong> ").append(passedTests).append("</p>\n");
        html.append("<p class=\"failed\"><strong>Failed:</strong> ").append(failedTests).append("</p>\n");
        html.append("<p class=\"skipped\"><strong>Skipped:</strong> ").append(skippedTests).append("</p>\n");
        html.append("</div>\n");
        
        html.append("<h2>Test Details</h2>\n");
        html.append("<table>\n");
        html.append("<tr><th>Test Name</th><th>Status</th><th>Duration (ms)</th><th>Message</th></tr>\n");
        
        for (TestReport report : testReports) {
            html.append("<tr>\n");
            html.append("<td>").append(report.getTestName()).append("</td>\n");
            html.append("<td class=\"").append(report.getStatus().toLowerCase()).append("\">")
                .append(report.getStatus()).append("</td>\n");
            html.append("<td>").append(report.getDurationMs()).append("</td>\n");
            html.append("<td>").append(report.getMessage() != null ? report.getMessage() : "").append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        html.append("</body>\n</html>");
        
        return html.toString();
    }

    /**
     * Builds text content for the report.
     *
     * @return the text string
     */
    private String buildTextContent() {
        long totalTests = testReports.size();
        long passedTests = testReports.stream().filter(r -> r.getStatus().equals("PASSED")).count();
        long failedTests = testReports.stream().filter(r -> r.getStatus().equals("FAILED")).count();
        long skippedTests = totalTests - passedTests - failedTests;
        
        StringBuilder text = new StringBuilder();
        text.append("====================================\n");
        text.append("TEST EXECUTION REPORT\n");
        text.append("====================================\n\n");
        
        text.append("Execution Start: ").append(executionStartTime).append("\n\n");
        
        text.append("SUMMARY\n");
        text.append("-------\n");
        text.append("Total Tests: ").append(totalTests).append("\n");
        text.append("Passed: ").append(passedTests).append("\n");
        text.append("Failed: ").append(failedTests).append("\n");
        text.append("Skipped: ").append(skippedTests).append("\n\n");
        
        text.append("TEST DETAILS\n");
        text.append("-------\n");
        
        for (TestReport report : testReports) {
            text.append("Test: ").append(report.getTestName()).append("\n");
            text.append("Status: ").append(report.getStatus()).append("\n");
            text.append("Duration: ").append(report.getDurationMs()).append(" ms\n");
            if (report.getMessage() != null) {
                text.append("Message: ").append(report.getMessage()).append("\n");
            }
            text.append("\n");
        }
        
        text.append("====================================\n");
        text.append("END OF REPORT\n");
        text.append("====================================\n");
        
        return text.toString();
    }

    /**
     * Gets the number of tests in the report.
     *
     * @return the number of test reports
     */
    public int getTestCount() {
        return testReports.size();
    }

    /**
     * Gets the number of passed tests.
     *
     * @return the count of passed tests
     */
    public long getPassedCount() {
        return testReports.stream().filter(r -> r.getStatus().equals("PASSED")).count();
    }

    /**
     * Gets the number of failed tests.
     *
     * @return the count of failed tests
     */
    public long getFailedCount() {
        return testReports.stream().filter(r -> r.getStatus().equals("FAILED")).count();
    }
}
