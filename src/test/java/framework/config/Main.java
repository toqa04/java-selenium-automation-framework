package framework.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.reporting.ExcelExecutionListener;
import framework.reporting.ReportListener;

/**
 * Console runner that discovers and executes available TestNG screens.
 */
public final class Main {

    private Main() {
        // Utility class.
    }

    public static void main(String[] args) {
        List<Class<?>> availableTests = discoverTests();
        if (availableTests.isEmpty()) {
            System.err.println("No test classes found. Run Maven test compilation first.");
            return;
        }

        printMenu(availableTests);
        try (Scanner scanner = new Scanner(System.in)) {
            String choice = scanner.nextLine().trim();
            List<Class<?>> selectedTests = selectTests(choice, availableTests, scanner);
            if (selectedTests.isEmpty()) {
                System.out.println("No valid screens selected. Execution cancelled.");
                return;
            }
            System.out.println("Selected tests: " + selectedTests.stream()
                    .map(Main::displayName)
                    .collect(Collectors.joining(", ")));
            runTests(selectedTests);
        }
    }

    private static void printMenu(List<Class<?>> availableTests) {
        System.out.println("===============================================");
        System.out.println(" OrangeHRM Automation Test Runner");
        System.out.println("===============================================");
        System.out.println("Available screens/tests:");
        System.out.println("1) All screens");
        for (int i = 0; i < availableTests.size(); i++) {
            System.out.println((i + 2) + ") " + displayName(availableTests.get(i)));
        }
        System.out.println("Y) Run multiple screens (numbers separated by commas)");
        System.out.print("Choose an option: ");
    }

    private static List<Class<?>> discoverTests() {
        Path testClasses = Paths.get("target", "test-classes", "tests");
        if (!Files.exists(testClasses)) {
            return Collections.emptyList();
        }

        List<Class<?>> classes = new ArrayList<>();
        try {
            Files.walk(testClasses)
                    .filter(Main::isTestClassFile)
                    .forEach(path -> loadTestClass(path, testClasses, classes));
        } catch (IOException e) {
            throw new IllegalStateException("Could not scan compiled test classes", e);
        }

        classes.sort(Comparator.comparing(Main::displayName));
        return classes;
    }

    private static void loadTestClass(Path path, Path root, List<Class<?>> classes) {
        String className = toClassName(path, root);
        try {
            Class<?> clazz = Class.forName(className);
            if (!Modifier.isAbstract(clazz.getModifiers()) && hasTestMethod(clazz)) {
                classes.add(clazz);
            }
        } catch (LinkageError | ClassNotFoundException e) {
            System.err.println("Could not load test class: " + className + " (" + e.getMessage() + ")");
        }
    }

    private static boolean isTestClassFile(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith("Test.class") && !name.contains("$");
    }

    private static String toClassName(Path classFile, Path root) {
        String relative = root.relativize(classFile).toString();
        return "tests." + relative.substring(0, relative.length() - ".class".length())
                .replace(File.separatorChar, '.');
    }

    private static boolean hasTestMethod(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                return true;
            }
        }
        return false;
    }

    private static String displayName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return simpleName.endsWith("Test")
                ? simpleName.substring(0, simpleName.length() - "Test".length())
                : simpleName;
    }

    private static List<Class<?>> selectTests(String choice, List<Class<?>> available, Scanner scanner) {
        if ("1".equals(choice)) {
            return new ArrayList<>(available);
        }
        if ("y".equalsIgnoreCase(choice)) {
            System.out.print("Enter screen numbers separated by commas: ");
            choice = scanner.nextLine().trim();
        }

        List<Class<?>> selected = new ArrayList<>();
        for (String token : choice.split(",")) {
            try {
                int option = Integer.parseInt(token.trim());
                int index = option - 2;
                if (index >= 0 && index < available.size() && !selected.contains(available.get(index))) {
                    selected.add(available.get(index));
                }
            } catch (NumberFormatException ignored) {
                // Ignore invalid menu tokens and keep valid selections.
            }
        }
        return selected;
    }

    private static void runTests(List<Class<?>> selectedTests) {
        XmlSuite suite = new XmlSuite();
        suite.setName("OrangeHRM Dynamic Suite");
        suite.setParallel(XmlSuite.ParallelMode.NONE);

        XmlTest xmlTest = new XmlTest(suite);
        xmlTest.setName("Selected OrangeHRM Screens");
        List<XmlClass> xmlClasses = selectedTests.stream()
                .map(testClass -> new XmlClass(testClass.getName()))
                .collect(Collectors.toList());
        xmlTest.setXmlClasses(xmlClasses);

        String reportDirectory = getReportDirectory();
        ExcelExecutionListener excelListener = new ExcelExecutionListener(reportDirectory);
        TestNG testNG = new TestNG();
        testNG.setXmlSuites(Collections.singletonList(suite));
        testNG.setOutputDirectory("test-output");
        testNG.addListener(new ReportListener());
        testNG.addListener(excelListener);

        try {
            testNG.run();
        } finally {
            String excelReport = excelListener.generateReport();
            System.out.println("===============================================");
            System.out.println("Execution finished.");
            System.out.println("Excel report: " + excelReport);
            System.out.println("Screenshots on failure: " + reportDirectory + File.separator + "screenshots");
            System.out.println("===============================================");
        }
    }

    private static String getReportDirectory() {
        try {
            return ConfigManager.getInstance().getReportsDirectory();
        } catch (RuntimeException exception) {
            System.err.println("Could not load reports directory. Using reports/excel.");
            return "reports/excel";
        }
    }
}
