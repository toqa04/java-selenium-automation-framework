package framework.execution;

import framework.registry.TestRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides various test selection strategies.
 * 
 * Factory for creating ExecutionStrategy instances.
 * Encapsulates different ways to select tests for execution.
 * 
 * Strategies include:
 * - All tests
 * - Tests by category
 * - Tests by priority
 * - Tests by name pattern
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class TestSelector {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSelector.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private TestSelector() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Creates a strategy to run all registered tests.
     *
     * @return execution strategy for all tests
     */
    public static ExecutionStrategy selectAllTests() {
        return () -> {
            List<String> testIds = TestRegistry.getAllTestIds();
            logger.info("Selected {} tests (all)", testIds.size());
            return testIds;
        };
    }

    /**
     * Creates a strategy to run tests by category.
     *
     * @param category the test category
     * @return execution strategy for category
     */
    public static ExecutionStrategy selectByCategory(String category) {
        return () -> {
            List<TestRegistry.TestMetadata> tests = TestRegistry.getTestsByCategory(category);
            List<String> testIds = tests.stream()
                .map(TestRegistry.TestMetadata::getTestName)
                .collect(Collectors.toList());
            logger.info("Selected {} tests in category: {}", testIds.size(), category);
            return testIds;
        };
    }

    /**
     * Creates a strategy to run tests by priority.
     *
     * @param priority the priority level (1 = highest)
     * @return execution strategy for priority
     */
    public static ExecutionStrategy selectByPriority(int priority) {
        return () -> {
            List<TestRegistry.TestMetadata> tests = TestRegistry.getTestsByPriority(priority);
            List<String> testIds = tests.stream()
                .map(TestRegistry.TestMetadata::getTestName)
                .collect(Collectors.toList());
            logger.info("Selected {} tests with priority: {}", testIds.size(), priority);
            return testIds;
        };
    }

    /**
     * Creates a strategy to run tests matching a name pattern.
     *
     * @param pattern the name pattern (regex)
     * @return execution strategy for pattern
     */
    public static ExecutionStrategy selectByNamePattern(String pattern) {
        return () -> {
            List<String> testIds = TestRegistry.getAllTestIds().stream()
                .filter(id -> id.matches(pattern))
                .collect(Collectors.toList());
            logger.info("Selected {} tests matching pattern: {}", testIds.size(), pattern);
            return testIds;
        };
    }

    /**
     * Creates a custom strategy.
     *
     * @param strategy the custom execution strategy
     * @return the provided strategy
     */
    public static ExecutionStrategy custom(ExecutionStrategy strategy) {
        return strategy;
    }
}
