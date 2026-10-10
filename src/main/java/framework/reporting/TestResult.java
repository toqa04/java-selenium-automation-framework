package framework.reporting;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable model for a single test execution result.
 */
public final class TestResult {
    private final String testId;
    private final String screenId;
    private final String screenName;
    private final String testName;
    private final TestStatus status;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final long duration;
    private final String errorMessage;
    private final String exceptionType;
    private final String screenshotPath;

    public TestResult(String testId,
                      String screenId,
                      String screenName,
                      String testName,
                      TestStatus status,
                      LocalDateTime startTime,
                      LocalDateTime endTime,
                      long duration,
                      String errorMessage,
                      String exceptionType,
                      String screenshotPath) {
        this.testId = testId;
        this.screenId = screenId;
        this.screenName = screenName;
        this.testName = testName;
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.errorMessage = errorMessage;
        this.exceptionType = exceptionType;
        this.screenshotPath = screenshotPath;
    }

    public String getTestId() { return testId; }
    public String getScreenId() { return screenId; }
    public String getScreenName() { return screenName; }
    public String getTestName() { return testName; }
    public TestStatus getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public long getDuration() { return duration; }
    public String getErrorMessage() { return errorMessage; }
    public String getExceptionType() { return exceptionType; }
    public String getScreenshotPath() { return screenshotPath; }
}
