package framework.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import framework.config.ConfigManager;

/**
 * TestNG listener for test reporting and statistics.
 *
 * Collects test execution data and provides reporting capabilities. Can be
 * extended to generate reports in various formats.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class ReportListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(ReportListener.class);
    private static final ReportBuilder reportBuilder = new ReportBuilder(
            ConfigManager.getInstance().getReportsDirectory());

    @Override
    public void onTestStart(ITestResult result) {
        logger.debug("Test started: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        TestReport report = new TestReport.Builder(result.getMethod().getMethodName())
                .status("PASSED")
                .durationMs(duration)
                .build();

        reportBuilder.addTestReport(report);
        logger.info("Test passed: {} ({}ms)", result.getMethod().getMethodName(), duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";

        TestReport report = new TestReport.Builder(result.getMethod().getMethodName())
                .status("FAILED")
                .durationMs(duration)
                .message(errorMessage)
                .errorStackTrace(getStackTrace(throwable))
                .build();

        reportBuilder.addTestReport(report);
        logger.error("Test failed: {} ({}ms)", result.getMethod().getMethodName(), duration);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TestReport report = new TestReport.Builder(result.getMethod().getMethodName())
                .status("SKIPPED")
                .message("Test skipped")
                .build();

        reportBuilder.addTestReport(report);
        logger.warn("Test skipped: {}", result.getMethod().getMethodName());
    }

    /**
     * Converts a throwable to a stack trace string.
     *
     * @param throwable the exception
     * @return the stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the report builder instance. Can be used to generate final reports
     * after test suite completion.
     *
     * @return the ReportBuilder instance
     */
    public static ReportBuilder getReportBuilder() {
        return reportBuilder;
    }
}
