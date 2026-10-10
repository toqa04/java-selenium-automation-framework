package framework.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registry for discovering and managing available tests.
 * 
 * Implements the Registry design pattern.
 * Allows dynamic test selection and execution without modifying framework.
 * 
 * Test metadata includes:
 * - Test name
 * - Test class
 * - Test method
 * - Category/Module
 * - Priority
 * - Description
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class TestRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(TestRegistry.class);
    private static final Map<String, TestMetadata> testRegistry = new HashMap<>();

    /**
     * Registers a test in the registry.
     *
     * @param testId the unique test identifier
     * @param metadata the test metadata
     */
    public static void registerTest(String testId, TestMetadata metadata) {
        if (testRegistry.containsKey(testId)) {
            logger.warn("Test already registered: {}", testId);
            return;
        }
        
        testRegistry.put(testId, metadata);
        logger.info("Test registered: {} ({})", testId, metadata.getTestClassName());
    }

    /**
     * Gets test metadata by ID.
     *
     * @param testId the test identifier
     * @return the test metadata or null if not found
     */
    public static TestMetadata getTest(String testId) {
        TestMetadata metadata = testRegistry.get(testId);
        if (metadata == null) {
            logger.warn("Test not found in registry: {}", testId);
        }
        return metadata;
    }

    /**
     * Gets all registered tests.
     *
     * @return list of all test metadata
     */
    public static List<TestMetadata> getAllTests() {
        return new ArrayList<>(testRegistry.values());
    }

    /**
     * Gets tests filtered by category.
     *
     * @param category the test category
     * @return list of tests in the category
     */
    public static List<TestMetadata> getTestsByCategory(String category) {
        return testRegistry.values().stream()
            .filter(t -> category.equals(t.getCategory()))
            .collect(Collectors.toList());
    }

    /**
     * Gets tests filtered by priority.
     *
     * @param priority the priority level
     * @return list of tests with that priority
     */
    public static List<TestMetadata> getTestsByPriority(int priority) {
        return testRegistry.values().stream()
            .filter(t -> priority == t.getPriority())
            .collect(Collectors.toList());
    }

    /**
     * Gets all registered test IDs.
     *
     * @return list of test IDs
     */
    public static List<String> getAllTestIds() {
        return new ArrayList<>(testRegistry.keySet());
    }

    /**
     * Checks if a test is registered.
     *
     * @param testId the test identifier
     * @return true if test is registered
     */
    public static boolean isTestRegistered(String testId) {
        return testRegistry.containsKey(testId);
    }

    /**
     * Unregisters a test (for cleanup or testing).
     *
     * @param testId the test identifier
     */
    public static void unregisterTest(String testId) {
        if (testRegistry.remove(testId) != null) {
            logger.info("Test unregistered: {}", testId);
        }
    }

    /**
     * Clears all registered tests.
     */
    public static void clear() {
        testRegistry.clear();
        logger.info("Test registry cleared");
    }

    /**
     * Gets the total number of registered tests.
     *
     * @return the number of tests
     */
    public static int getTestCount() {
        return testRegistry.size();
    }

    /**
     * Data class for test metadata.
     */
    public static class TestMetadata {
        private final String testName;
        private final String testClassName;
        private final String testMethodName;
        private final String category;
        private final int priority; // 1 = highest, 5 = lowest
        private final String description;

        public TestMetadata(String testName, String testClassName, String testMethodName,
                          String category, int priority, String description) {
            this.testName = testName;
            this.testClassName = testClassName;
            this.testMethodName = testMethodName;
            this.category = category;
            this.priority = priority;
            this.description = description;
        }

        public String getTestName() {
            return testName;
        }

        public String getTestClassName() {
            return testClassName;
        }

        public String getTestMethodName() {
            return testMethodName;
        }

        public String getCategory() {
            return category;
        }

        public int getPriority() {
            return priority;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "TestMetadata{" +
                    "testName='" + testName + '\'' +
                    ", testClassName='" + testClassName + '\'' +
                    ", category='" + category + '\'' +
                    ", priority=" + priority +
                    '}';
        }
    }
}
