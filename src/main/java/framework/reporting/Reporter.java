package framework.reporting;

/**
 * Minimal abstraction for test execution reporting.
 *
 * The execution layer depends only on this interface and the concrete implementation
 * is selected later by the reporting layer, without coupling to Apache POI or any other format.
 */
public interface Reporter {
    void generate(TestExecutionSummary summary, java.util.List<TestResult> results);
}
