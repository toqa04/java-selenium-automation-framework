package framework.reporting;

import java.time.LocalDateTime;

/**
 * Immutable model summarizing a full execution run.
 */
public final class TestExecutionSummary {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int total;
    private final int passed;
    private final int failed;
    private final int skipped;
    private final long totalDuration;

    public TestExecutionSummary(LocalDateTime startTime,
                               LocalDateTime endTime,
                               int total,
                               int passed,
                               int failed,
                               int skipped,
                               long totalDuration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.totalDuration = totalDuration;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getTotal() { return total; }
    public int getPassed() { return passed; }
    public int getFailed() { return failed; }
    public int getSkipped() { return skipped; }
    public long getTotalDuration() { return totalDuration; }
}
