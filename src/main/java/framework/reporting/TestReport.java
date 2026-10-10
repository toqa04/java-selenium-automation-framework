package framework.reporting;

/**
 * Data class representing a single test execution result.
 * 
 * Immutable data transfer object for test report information.
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class TestReport {
    
    private final String testName;
    private final String status; // PASSED, FAILED, SKIPPED
    private final long durationMs;
    private final String message;
    private final String screenshotPath;
    private final String errorStackTrace;

    /**
     * Constructs a TestReport with all fields.
     *
     * @param testName the test name
     * @param status the test status (PASSED, FAILED, SKIPPED)
     * @param durationMs the test execution duration in milliseconds
     * @param message optional message
     * @param screenshotPath optional path to screenshot
     * @param errorStackTrace optional error stack trace
     */
    public TestReport(String testName, String status, long durationMs, 
                     String message, String screenshotPath, String errorStackTrace) {
        this.testName = testName;
        this.status = status;
        this.durationMs = durationMs;
        this.message = message;
        this.screenshotPath = screenshotPath;
        this.errorStackTrace = errorStackTrace;
    }

    /**
     * Builder constructor for easier creation.
     */
    public static class Builder {
        private final String testName;
        private String status = "UNKNOWN";
        private long durationMs = 0;
        private String message;
        private String screenshotPath;
        private String errorStackTrace;

        public Builder(String testName) {
            this.testName = testName;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder screenshotPath(String screenshotPath) {
            this.screenshotPath = screenshotPath;
            return this;
        }

        public Builder errorStackTrace(String errorStackTrace) {
            this.errorStackTrace = errorStackTrace;
            return this;
        }

        public TestReport build() {
            return new TestReport(testName, status, durationMs, message, screenshotPath, errorStackTrace);
        }
    }

    // Getters

    public String getTestName() {
        return testName;
    }

    public String getStatus() {
        return status;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getMessage() {
        return message;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }

    @Override
    public String toString() {
        return "TestReport{" +
                "testName='" + testName + '\'' +
                ", status='" + status + '\'' +
                ", durationMs=" + durationMs +
                '}';
    }
}
