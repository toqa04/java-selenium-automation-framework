# Framework Architecture & Design Patterns

This document explains the architecture of the Selenium automation framework, the design patterns used, and the reasoning behind them.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Design Patterns](#design-patterns)
3. [Package Responsibilities](#package-responsibilities)
4. [Future Parallel Execution](#future-parallel-execution)
5. [Adding New Browsers](#adding-new-browsers)

---

## Architecture Overview

### Layered Architecture

The framework is organized into distinct layers:

```
┌─────────────────────────────────────────┐
│       TEST LAYER (tests/)               │ User-facing test cases
├─────────────────────────────────────────┤
│       PAGE OBJECT LAYER (pages/)        │ Page abstractions
├─────────────────────────────────────────┤
│       FRAMEWORK LAYER (framework/)      │ Core automation logic
├─────────────────────────────────────────┤
│       INFRASTRUCTURE LAYER              │ External libraries
│       (Selenium, TestNG, POI, Logback)  │
└─────────────────────────────────────────┘
```

### Separation of Concerns

**Framework Code** (in `framework/` package):

- Completely independent of specific applications
- No hardcoded locators or business logic
- Reusable across multiple projects
- Can be deployed as a library

**Test Code** (in `tests/` package):

- Application-specific page objects and tests
- Business logic and workflows
- Test data and assertions
- Depends on framework, not vice versa

This separation means:

- Framework updates don't break tests
- Tests can be updated independently
- Framework can be reused in other projects

---

## Design Patterns

### 1. Page Object Model (POM)

**Location**: `framework.pages` (base classes), `tests/[screen]/pages` (implementations)

**Purpose**: Encapsulate page elements and interactions

**Structure**:

```java
// BasePage provides common functionality
public abstract class BasePage {
    protected WebDriver driver;
    protected WaitManager waitManager;

    // Common actions
    protected WebElement getElement(By locator)
    protected void click(By locator)
    protected void sendKeys(By locator, String text)
}

// Specific pages extend BasePage
public class LoginPage extends BasePage {
    private static final By USERNAME = By.id("username");

    public void login(String user, String pass) {
        sendKeys(USERNAME, user);
        // ...
    }
}
```

**Benefits**:

- Tests focus on what, not how
- Easy to maintain locators
- Reduces duplication
- Improves readability

**Why Selected**:

- Industry standard for web automation
- Naturally fits page-based application testing
- Promotes maintainability at scale

---

### 2. Factory Pattern

**Location**: `framework.driver`

**Purpose**: Centralize WebDriver creation logic

**Implementation**:

```java
public class WebDriverFactory {
    public static WebDriver createDriver(DriverOptions options) {
        return switch (options.getBrowserType()) {
            case CHROME -> createChromeDriver(options);
            case FIREFOX -> createFirefoxDriver(options);
            case SAFARI -> createSafariDriver(options);
            case EDGE -> createEdgeDriver(options);
        };
    }
}
```

**Benefits**:

- Single point for driver configuration
- Easy to add new browsers
- Consistent driver setup
- Handles WebDriverManager lifecycle

**Why Selected**:

- Hides complex creation logic
- Makes adding browsers trivial
- Decouples driver creation from usage

---

### 3. ThreadLocal Singleton Pattern

**Location**: `framework.driver.DriverManager`

**Purpose**: Thread-safe WebDriver management for parallel execution

**Implementation**:

```java
public class DriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal
        = new ThreadLocal<>();

    public static void initializeDriver(Configuration config) {
        WebDriver driver = WebDriverFactory.createDriver(...);
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
}
```

**Benefits**:

- Each thread gets independent driver
- No synchronization issues
- Foundation for parallel execution
- Clean resource management

**Why Selected**:

- Essential for parallel test execution
- Thread-safe without locks
- Standard Java pattern

---

### 4. Template Method Pattern

**Location**: `tests.base.BaseTest`

**Purpose**: Define test lifecycle structure, allow customization

**Implementation**:

```java
public abstract class BaseTest {

    @BeforeMethod
    public final void setUp(ITestContext context) {
        // Framework setup
        configuration = ConfigLoader.load();
        DriverManager.initializeDriver(configuration);

        // Customization hook
        beforeTestSetup(context);
    }

    protected void beforeTestSetup(ITestContext context) {
        // Override in subclass if needed
    }

    @AfterMethod
    public final void tearDown(ITestContext context) {
        afterTestTeardown(context);
        DriverManager.quitDriver();
    }

    protected void afterTestTeardown(ITestContext context) {
        // Override in subclass if needed
    }
}
```

**Benefits**:

- Consistent test setup/teardown
- Ensures driver cleanup
- Allows test-specific customization
- Prevents "forgot to cleanup" bugs

**Why Selected**:

- Natural fit with TestNG lifecycle
- Enforces best practices
- Flexible for diverse needs

---

### 5. Production-Hardening Utilities

**Location**: `framework.utils`

**Purpose**: Handle common runtime UI issues without polluting page objects or tests.

**Key Components**:

- `LoadingManager`: waits for the document to become ready and checks underlying AJAX activity using JavaScript.
- `UnexpectedUiHandler`: dismisses unexpected alerts and generic overlays in a framework-neutral way.

**Benefits**:

- Keeps page objects generic
- Reduces flaky tests caused by transient UI states
- Avoids application-specific logic in the framework layer

**Why Selected**:

- It addresses real production reliability concerns without overengineering the design
- The utilities are generic and safe to reuse across many screen objects

---

### 6. Strategy Pattern

**Location**: `framework.waits`, `framework.execution`

**Purpose**: Encapsulate different behaviors for waits and test execution

**Wait Strategies**:

```java
@FunctionalInterface
public interface WaitStrategy {
    WebElement execute(WebDriver driver);
}

// Client code
WebElement element = waitManager.waitForCustom(
    (driver) -> driver.findElement(By.id("dynamic"))
);
```

**Execution Strategies**:

```java
ExecutionStrategy allTests = TestSelector.selectAllTests();
ExecutionStrategy categoryTests = TestSelector.selectByCategory("Login");
ExecutionStrategy priorityTests = TestSelector.selectByPriority(1);
```

**Benefits**:

- Switch between strategies at runtime
- Easy to add new strategies
- Clean, composable logic

**Why Selected**:

- Necessary for different wait scenarios
- Supports dynamic test selection
- Functional approach in modern Java

---

### 7. Registry Pattern

**Location**: `framework.registry`

**Purpose**: Discover available screens and tests dynamically

**Implementation**:

```java
public class ScreenRegistry {
    private static final Map<String, ScreenMetadata> registry
        = new HashMap<>();

    public static void registerScreen(String id, ScreenMetadata meta) {
        registry.put(id, meta);
    }

    public static ScreenMetadata getScreen(String id) {
        return registry.get(id);
    }

    public static List<ScreenMetadata> getAllScreens() {
        return new ArrayList<>(registry.values());
    }
}
```

**Benefits**:

- Add screens without modifying framework
- Runtime discovery of available screens
- Supports test filtering and selection
- No hardcoded test lists

**Why Selected**:

- Enables true framework extensibility
- Essential for large projects (hundreds of screens)
- Supports dynamic test execution

---

### 7. Observer/Listener Pattern

**Location**: `framework.reporting`, `framework.screenshots`

**Purpose**: Decouple test execution from reporting/evidence collection

**Implementation**:

```java
@Listener
public class ReportListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        // Record successful test
        reportBuilder.addTestReport(...);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Record failure, capture screenshot
        screenshotManager.takeScreenshot(driver, testName);
    }
}
```

**Benefits**:

- Automatic screenshot capture
- No test code changes needed
- Extensible reporting
- Separation from test logic

**Why Selected**:

- TestNG provides listener interface
- Excellent for cross-cutting concerns
- Zero impact on test code

---

### 8. Facade Pattern

**Location**: `framework.waits.WaitManager`, `framework.config.ConfigLoader`

**Purpose**: Simplify complex subsystems

**Implementation**:

```java
// Complex: ExpectedConditions, WebDriverWait, multiple methods
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

// Simplified: WaitManager
waitManager.waitForVisibility(locator);
```

**Benefits**:

- Simpler client code
- Encapsulates complexity
- Consistent interface

**Why Selected**:

- Reduces learning curve
- Makes framework approachable
- Standard Selenium usage pattern

---

## Package Responsibilities

### `framework.config`

**Responsibility**: Configuration management and environment handling

**Classes**:

- `Configuration`: Configuration data container
- `ConfigLoader`: Loads properties from files
- `Environment`: Enum for dev/staging/prod

**Design**: Immutable, read-only configuration objects

---

### `framework.driver`

**Responsibility**: WebDriver lifecycle and creation

**Classes**:

- `DriverManager`: ThreadLocal driver storage (Singleton per thread)
- `WebDriverFactory`: Creates driver instances (Factory pattern)
- `BrowserType`: Enum for supported browsers
- `DriverOptions`: Driver configuration holder

**Design**: No global static driver, fully thread-safe

---

### `framework.pages`

**Responsibility**: Page Object Model base classes

**Classes**:

- `BasePage`: Common page interactions (Template Method)
- `PageActions`: Advanced page interactions (Utility)

**Design**: Reusable across all page objects

---

### `framework.waits`

**Responsibility**: Wait condition management

**Classes**:

- `WaitManager`: Simplified wait interface (Facade)
- `ExplicitWait`: Selenium wait wrapper
- `WaitStrategy`: Custom wait behavior (Strategy)

**Design**: Encourages explicit waits over implicit

---

### `framework.utils`

**Responsibility**: Common utility functions

**Classes**:

- `StringUtils`: String manipulation
- `FileUtils`: File operations
- `DateUtils`: Date/time operations

**Design**: All static methods, no instance creation

---

### `framework.screenshots`

**Responsibility**: Screenshot capture and management

**Classes**:

- `ScreenshotManager`: Screenshot operations
- `ScreenshotListener`: Automatic failure capture (Listener)

**Design**: Centralized screenshot handling

---

### `framework.reporting`

**Responsibility**: Test reporting and metrics

**Classes**:

- `ReportBuilder`: Report generation (Builder pattern)
- `TestReport`: Test result data (DTO)
- `ReportListener`: TestNG integration (Listener)

**Design**: Flexible report formats, extensible

---

### `framework.excel`

**Responsibility**: Excel file operations

**Classes**:

- `ExcelReader`: Read test data from Excel
- `ExcelWriter`: Write results to Excel

**Design**: Simple API for test data

---

### `framework.registry`

**Responsibility**: Dynamic screen/test discovery

**Classes**:

- `ScreenRegistry`: Available screens registry
- `TestRegistry`: Available tests registry

**Design**: Runtime registration, no hardcoding

---

### `framework.execution`

**Responsibility**: Test execution strategies

**Classes**:

- `ExecutionStrategy`: Strategy interface
- `TestSelector`: Strategy implementations

**Design**: Pluggable execution strategies

---

### `framework.exceptions`

**Responsibility**: Custom exception hierarchy

**Classes**:

- `FrameworkException`: Base exception
- `DriverException`: WebDriver errors
- `PageException`: Page interaction errors
- `ConfigurationException`: Configuration errors

**Design**: Specific, catchable exceptions

---

## Future Parallel Execution

The framework is designed for parallel execution from the ground up:

### 1. ThreadLocal Driver Storage

```java
// Each thread gets its own driver
private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

@BeforeMethod
public void setUp(ITestContext context) {
    DriverManager.initializeDriver(configuration); // Thread-local set
}
```

### 2. Independent Page Objects

```java
// Each test creates its own page object instance
// No shared state
LoginPage page = new LoginPage(driver, config);
```

### 3. Test-Local Configuration

```java
// Configuration loaded per test
Configuration config = ConfigLoader.load(); // Per thread
DriverManager.initializeDriver(config);
```

### 4. Maven Surefire Configuration (in pom.xml)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

### 5. Enable Parallel Execution

```bash
# Run tests with 8 threads
mvn clean test -DthreadCount=8

# Run tests with 4 threads in parallel methods mode
mvn test -Dparallel=methods -DthreadCount=4
```

### How It Works

```
Thread 1: Test A
├─ Creates WebDriver (via DriverManager.initializeDriver)
├─ Stores in ThreadLocal
├─ Test executes
└─ Driver quit (in tearDown)

Thread 2: Test B
├─ Creates separate WebDriver
├─ Stores in different ThreadLocal
├─ Test executes independently
└─ Driver quit

Thread 3: Test C
├─ Same isolation...
```

**Result**: Tests run concurrently without interference

---

## Adding New Browsers

Add new browser support with minimal changes:

### 1. Extend BrowserType enum

```java
public enum BrowserType {
    CHROME("chrome", "Google Chrome"),
    FIREFOX("firefox", "Mozilla Firefox"),
    SAFARI("safari", "Apple Safari"),
    EDGE("edge", "Microsoft Edge"),
    OPERA("opera", "Opera Browser"); // ADD THIS

    // ...
}
```

### 2. Implement in WebDriverFactory

```java
public static WebDriver createDriver(DriverOptions options) {
    return switch (options.getBrowserType()) {
        case CHROME -> createChromeDriver(options);
        case FIREFOX -> createFirefoxDriver(options);
        case SAFARI -> createSafariDriver(options);
        case EDGE -> createEdgeDriver(options);
        case OPERA -> createOperaDriver(options); // ADD THIS
    };
}

private static WebDriver createOperaDriver(DriverOptions options) {
    WebDriverManager.operadriver().setup();
    OperaOptions operaOptions = new OperaOptions();

    if (options.isHeadless()) {
        operaOptions.addArguments("--headless");
    }

    return new OperaDriver(operaOptions);
}
```

### 3. Use new browser

```bash
mvn clean test -Dbrowser=opera
```

**No tests changed!** The factory handles everything.

---

## Why This Architecture?

### Problem: Enterprise Applications

- Hundreds of screens to test
- Large teams working on tests
- Need for maintenance and scalability
- Future parallel execution requirement

### Solution: Separation

1. **Framework** remains stable and reusable
2. **Tests** can be updated independently
3. **Adding screens** requires no framework changes
4. **Parallel execution** built in from the start

### Key Principles

- **DRY**: Code not repeated across tests
- **SOLID**: Single responsibility, open to extension
- **KISS**: No unnecessary complexity
- **Maintainability**: Easy to understand and modify
- **Scalability**: Handles hundreds of screens

---

## Summary

This framework provides:

- ✅ Clean separation (framework ↔ tests)
- ✅ Extensibility (new screens without framework changes)
- ✅ Maintainability (clear responsibilities, DRY)
- ✅ Scalability (thread-safe, parallel-ready)
- ✅ Flexibility (pluggable strategies, multiple browsers)
- ✅ Enterprise-ready (logging, reporting, screenshots)

The design prioritizes **practical enterprise automation** over unnecessary abstraction.
