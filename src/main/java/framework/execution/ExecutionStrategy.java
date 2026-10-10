package framework.execution;

import java.util.List;

/**
 * Strategy interface for test execution selection.
 *
 * Implements the Strategy design pattern. Different execution strategies can be
 * implemented for various test selection scenarios.
 *
 * Examples: - Run all tests - Run tests by category - Run tests by priority -
 * Run tests by name pattern
 *
 * @author Automation Team
 * @version 1.0.0
 */
@FunctionalInterface
public interface ExecutionStrategy {

    /**
     * Selects tests for execution.
     *
     * @return list of selected test IDs
     */
    List<String> selectTests();
}
