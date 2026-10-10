package framework.reporting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import framework.excel.ExcelReporter;

/**
 * Collects TestNG results and writes one Excel report when the suite finishes.
 */
public class ExcelExecutionListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExecutionListener.class);
    private final List<TestResult> results = Collections.synchronizedList(new ArrayList<>());
    private final LocalDateTime executionStart = LocalDateTime.now();
    private final String outputDirectory;

    public ExcelExecutionListener(String outputDirectory) {
        this.outputDirectory = outputDirectory == null || outputDirectory.trim().isEmpty()
                ? "reports/excel" : outputDirectory;
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        addResult(result, TestStatus.PASS);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        addResult(result, TestStatus.FAIL);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        addResult(result, TestStatus.SKIPPED);
    }

    private void addResult(ITestResult result, TestStatus status) {
        Throwable error = result.getThrowable();
        String className = result.getTestClass() == null ? "" : result.getTestClass().getName();
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        String screenName = simpleClassName.endsWith("Test")
                ? simpleClassName.substring(0, simpleClassName.length() - 4) : simpleClassName;
        String errorMessage = error == null ? "" : String.valueOf(error.getMessage());
        String exceptionType = error == null ? "" : error.getClass().getName();
        Object screenshotAttribute = result.getAttribute("screenshotPath");
        String screenshotPath = screenshotAttribute == null ? "" : screenshotAttribute.toString();
        long start = result.getStartMillis() > 0 ? result.getStartMillis() : System.currentTimeMillis();
        long end = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();

        results.add(new TestResult(
                screenName.toUpperCase() + "_001",
                screenName.toUpperCase(),
                screenName,
                result.getMethod().getMethodName(),
                status,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Math.max(0, end - start),
                errorMessage,
                exceptionType,
                screenshotPath));
    }

    @Override
    public void onFinish(ITestContext context) {
        // The suite finish callback is used instead of onFinish so all selected tests are included.
    }

    /**
     * Generates the final workbook. Main calls this after TestNG returns.
     */
    public String generateReport() {
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long duration = 0;
        synchronized (results) {
            for (TestResult result : results) {
                duration += result.getDuration();
                if (result.getStatus() == TestStatus.PASS) {
                    passed++;
                }
                if (result.getStatus() == TestStatus.FAIL) {
                    failed++;
                }
                if (result.getStatus() == TestStatus.SKIPPED) {
                    skipped++;
                }
            }
        }

        TestExecutionSummary summary = new TestExecutionSummary(
                executionStart, LocalDateTime.now(), results.size(), passed, failed, skipped, duration);
        ExcelReporter reporter = new ExcelReporter(outputDirectory);
        reporter.generate(summary, new ArrayList<>(results));
        String path = reporter.getLastReportPath();
        logger.info("Excel execution report generated: {}", path);
        return path;
    }
}
