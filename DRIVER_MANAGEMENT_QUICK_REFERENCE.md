# WebDriver Management Quick Reference

## For Test Developers

### Accessing the WebDriver

```java
public class LoginPageTest extends BaseTest {

    @Test
    public void testLogin() {
        // Framework automatically initializes driver in @BeforeMethod
        // Get driver via protected helper method
        WebDriver driver = getDriver();

        // Or work through page objects (preferred)
        LoginPage page = new LoginPage(getDriver(), getConfiguration());
        page.login("user", "pass");
    }
}
```

### Creating a New Test

```java
package tests.myscreen;

import framework.registry.TestRegistry;
import tests.base.BaseTest;
import tests.myscreen.pages.MyScreenPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyScreenTest extends BaseTest {

    private MyScreenPage page;

    @BeforeClass
    public void registerTests() {
        TestRegistry.registerTest("SCREEN_001",
            new TestRegistry.TestMetadata(
                "SCREEN_001",
                "tests.myscreen.MyScreenTest",
                "testSomething",
                "MyScreen",
                1,  // priority
                "Test description"
            )
        );
    }

    @Test(description = "Test something")
    public void testSomething() {
        // Driver is already initialized by BaseTest.setUp()
        page = new MyScreenPage(getDriver(), getConfiguration());

        // Interact with page
        page.performAction();

        // Verify results
        Assert.assertTrue(page.isSuccess());
    }

    // tearDown() is handled by BaseTest.tearDown()
    // Driver is automatically quit after test
}
```

### Creating a Page Object

```java
package tests.myscreen.pages;

import framework.config.Configuration;
import framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyScreenPage extends BasePage {

    // Define locators as private constants
    private static final By ACTION_BUTTON = By.id("action-btn");
    private static final By SUCCESS_MESSAGE = By.className("success");

    // Constructor
    public MyScreenPage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
    }

    // Page actions as public methods
    public void performAction() {
        click(ACTION_BUTTON);
    }

    public boolean isSuccess() {
        return isElementDisplayed(SUCCESS_MESSAGE);
    }
}
```

### Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginPageTest

# Run specific test method
mvn clean test -Dtest=LoginPageTest#testValidLogin

# Different browsers
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=safari
mvn clean test -Dbrowser=edge

# Environment
mvn clean test -Denvironment=staging
mvn clean test -Denvironment=prod

# Headless (for CI/CD)
mvn clean test -Dbrowser.headless=true

# Parallel execution
mvn clean test -DthreadCount=8
```

---

## For Framework Developers

### Understanding the Driver Lifecycle

1. **Test Start** → `@BeforeMethod` in BaseTest
2. **Configuration Load** → ConfigLoader.load()
3. **Driver Init** → DriverManager.initializeDriver(config)
4. **Driver Creation** → WebDriverFactory.createDriver(options)
5. **Browser Launch** → Chrome/Firefox/Safari/Edge starts
6. **Test Execution** → Test method runs
7. **Browser Operations** → Via page objects
8. **Test End** → Test method returns
9. **Driver Cleanup** → `@AfterMethod` in BaseTest
10. **Browser Close** → DriverManager.quitDriver()

### DriverManager API

```java
// Initialize driver from configuration
DriverManager.initializeDriver(Configuration config);

// Initialize with specific options (override config)
DriverManager.initializeDriver(DriverOptions options);

// Get driver for current thread
WebDriver driver = DriverManager.getDriver();  // Throws DriverException if null

// Get driver options
DriverOptions opts = DriverManager.getDriverOptions();

// Check if initialized
if (DriverManager.isDriverInitialized()) { ... }

// Quit and cleanup
DriverManager.quitDriver();

// Get debug info
String info = DriverManager.getDriverInfo();
```

### WebDriverFactory API

```java
// Only public method - creates WebDriver
WebDriver driver = WebDriverFactory.createDriver(DriverOptions options);

// Returns:
// - ChromeDriver if options.browserType == CHROME
// - FirefoxDriver if options.browserType == FIREFOX
// - SafariDriver if options.browserType == SAFARI
// - EdgeDriver if options.browserType == EDGE

// Throws DriverException on failure
```

### BrowserType Enum

```java
// All supported browsers
BrowserType chrome = BrowserType.CHROME;
BrowserType firefox = BrowserType.FIREFOX;
BrowserType safari = BrowserType.SAFARI;
BrowserType edge = BrowserType.EDGE;

// Parse from string (case-insensitive)
BrowserType browser = BrowserType.fromString("chrome");  // → CHROME
BrowserType browser = BrowserType.fromString("FIREFOX"); // → FIREFOX
BrowserType browser = BrowserType.fromString("unknown");  // → IllegalArgumentException

// Get values
String name = browser.getName();          // "chrome"
String display = browser.getDisplayName(); // "Google Chrome"
```

### DriverOptions API

```java
// Create from configuration (recommended)
DriverOptions options = DriverOptions.fromConfiguration(config);

// Or create manually
DriverOptions options = new DriverOptions(
    BrowserType.CHROME,
    true,   // headless
    false,  // maximized
    10,     // implicitWaitSeconds
    30,     // pageLoadTimeoutSeconds
    30      // scriptTimeoutSeconds
);

// Access properties
BrowserType browser = options.getBrowserType();
boolean headless = options.isHeadless();
boolean max = options.isMaximized();
int implicitWait = options.getImplicitWaitSeconds();
int pageLoadTimeout = options.getPageLoadTimeoutSeconds();
int scriptTimeout = options.getScriptTimeoutSeconds();
```

### Configuration Integration

Configuration properties that affect driver:

```properties
# Browser type (CHROME, FIREFOX, SAFARI, EDGE)
browser=chrome

# Headless mode
headless=false

# Window maximization
maximize=true

# Wait times (in seconds)
wait.implicit.seconds=0
wait.pageload.seconds=30

# Environment-specific overrides
# dev.properties, staging.properties, prod.properties
```

---

## Thread Safety for Parallel Tests

### How It Works

```
Test Runner (TestNG with 4 threads)
│
├─ Thread-1 (Test A)              ├─ Thread-2 (Test B)
│  ├─ setUp()                     │  ├─ setUp()
│  │  └─ ThreadLocal[1] = Driver1 │  │  └─ ThreadLocal[2] = Driver2
│  ├─ test()                      │  ├─ test()
│  │  └─ uses Driver1             │  │  └─ uses Driver2
│  └─ tearDown()                  │  └─ tearDown()
│     └─ quit Driver1             │     └─ quit Driver2
│        clear ThreadLocal[1]     │     clear ThreadLocal[2]
```

### No Conflicts

- Thread-1's driver stays in Thread-1's ThreadLocal
- Thread-2's driver stays in Thread-2's ThreadLocal
- No synchronization needed
- No blocking
- Tests run truly in parallel

### Running Parallel Tests

```bash
# 4 threads (default in pom.xml)
mvn clean test

# 8 threads
mvn clean test -DthreadCount=8

# 16 threads
mvn clean test -DthreadCount=16

# No code changes needed - ThreadLocal handles it
```

---

## Common Patterns

### Pattern 1: Standard Test Class

```java
public class MyTest extends BaseTest {

    @Test
    public void testSomething() {
        // Driver available via getDriver()
        WebDriver driver = getDriver();
        // OR via page object
        MyPage page = new MyPage(getDriver(), getConfiguration());
    }

    // setUp() and tearDown() handled by BaseTest
}
```

### Pattern 2: Custom Setup/Teardown

```java
public class MyTest extends BaseTest {

    private MyData data;

    @Override
    protected void beforeTestSetup(ITestContext context) {
        // Called after framework setup, before test
        data = loadTestData();
    }

    @Override
    protected void afterTestTeardown(ITestContext context) {
        // Called after test, before framework cleanup
        if (data != null) {
            data.cleanup();
        }
    }

    @Test
    public void testSomething() {
        // Use data loaded in beforeTestSetup
        MyPage page = new MyPage(getDriver(), getConfiguration());
    }
}
```

### Pattern 3: Page Object

```java
public class HomePage extends BasePage {

    private static final By SEARCH = By.id("search");
    private static final By RESULTS = By.className("results");

    public HomePage(WebDriver driver, Configuration config) {
        super(driver, config);
    }

    public void search(String query) {
        sendKeys(SEARCH, query);
        click(SEARCH);  // Or press Enter
    }

    public List<String> getResults() {
        List<WebElement> elements = driver.findElements(RESULTS);
        return elements.stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
```

---

## Troubleshooting

### "WebDriver not initialized"

```
Cause: Accessing driver before setUp() or outside test scope
Fix: Ensure test extends BaseTest and getDriver() is called in @Test method
```

### "NullPointerException on driver"

```
Cause: Accessing driver after tearDown() has quit it
Fix: Don't access driver in afterTestTeardown() after base tearDown
```

### "Driver for different browser"

```
Cause: Not passing browser parameter
Fix: mvn test -Dbrowser=firefox
```

### "Tests interfering with each other" (parallel)

```
Cause: Shared static state in test or page object
Fix: Remove all static fields, use instance variables
```

### "Timeout waiting for element"

```
Cause: Element wait timeout too short
Fix: Increase wait.explicit.seconds in properties file
    wait.explicit.seconds=20
```

---

## Extension Examples

### Adding New Browser Support

**Step 1**: Add to BrowserType enum

```java
public enum BrowserType {
    // ... existing
    CHROMIUM("chromium", "Chromium");
}
```

**Step 2**: Add to WebDriverFactory

```java
public static WebDriver createDriver(DriverOptions options) {
    return switch (options.getBrowserType()) {
        // ... existing cases
        case CHROMIUM -> createChromiumDriver(options);
    };
}

private static WebDriver createChromiumDriver(DriverOptions options) {
    WebDriverManager.chromiumdriver().setup();
    ChromiumOptions opts = new ChromiumOptions();
    // ... configure options
    return new ChromiumDriver(opts);
}
```

**Step 3**: Use it

```bash
mvn test -Dbrowser=chromium
```

### Custom Driver Options

**Override in test**:

```java
public class MyTest extends BaseTest {

    @Override
    protected void beforeTestSetup(ITestContext context) {
        // Create custom options
        DriverOptions customOpts = new DriverOptions(
            BrowserType.CHROME,
            false,  // not headless for debugging
            true,   // maximize
            15,     // longer implicit wait
            45,
            30
        );

        // Reinitialize with custom options
        DriverManager.initializeDriver(customOpts);
    }
}
```

---

## Best Practices

### ✅ DO

- Extend BaseTest in all test classes
- Use getDriver() or page objects to access WebDriver
- Keep page objects simple (locators + actions)
- Use page methods, not raw WebDriver calls
- Let framework handle setup/teardown
- Override hooks (beforeTestSetup) for custom setup
- Use WaitManager for waits (built into BasePage)
- Configure through properties files

### ❌ DON'T

- Instantiate WebDriver directly (new ChromeDriver())
- Use static WebDriver fields
- Access driver outside test scope
- Manipulate ThreadLocal directly
- Override setUp/tearDown (use hooks instead)
- Call driver.manage().timeouts() directly (use WaitManager)
- Hardcode browser type in tests (use configuration)

---

## Performance Tips

1. **Parallel Execution**: Use `-DthreadCount=8` for 8x faster tests
2. **Headless Mode**: Use `-Dbrowser.headless=true` for CI/CD (faster)
3. **Reuse Configuration**: Load once per thread (already done)
4. **Minimize Navigation**: Use relative URLs when possible
5. **Efficient Locators**: Use IDs > CSS > XPath for speed

---

## Summary

**In 30 Seconds**:

1. Extend BaseTest in your test class
2. Driver is automatically initialized in setUp()
3. Access via getDriver() or page objects
4. Driver is automatically quit in tearDown()
5. Framework handles all threading & lifecycle

**No Special Code Needed** - Just write tests! 🚀

---

**For Complete Reference**: See `README.md` section "WebDriver Management & Lifecycle"  
**For Architecture Details**: See `ARCHITECTURE.md`  
**For Adding Screens**: See `ADDING_NEW_SCREENS.md`
