# Quick Start Guide

Get up and running with the Selenium Automation Framework in 5 minutes.

## Prerequisites

- **Java 11**: `java -version`
- **Maven 3.6+**: `mvn -version`
- **Git** (optional): `git --version`

## Step 1: Setup (1 minute)

### Clone/Download the Framework

```bash
cd c:\Users\<your-user>\Desktop
cd automation
```

### Install Dependencies

```bash
mvn clean install
```

This downloads all required libraries: Selenium, TestNG, POI, etc.

## Step 2: Configure the Application URL (1 minute)

Edit `src/test/resources/config/config-dev.properties`:

```properties
baseUrl=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
```

Replace it with your actual application URL, or pass it at runtime using `-DbaseUrl=...`.

## Step 3: Create Your First Test (2 minutes)

### Create Page Object

Create file: `src/test/java/tests/example/pages/ExamplePage.java`

```java
package tests.example.pages;

import framework.config.Configuration;
import framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ExamplePage extends BasePage {

    private static final By PAGE_TITLE = By.tagName("h1");

    public ExamplePage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }
}
```

### Create Test Class

Create file: `src/test/java/tests/example/ExampleTest.java`

```java
package tests.example;

import framework.registry.TestRegistry;
import tests.base.BaseTest;
import tests.example.pages.ExamplePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExampleTest extends BaseTest {

    private ExamplePage page;

    @BeforeClass
    public void registerTests() {
        TestRegistry.registerTest("EX_001",
            new TestRegistry.TestMetadata(
                "EX_001",
                "tests.example.ExampleTest",
                "testPageTitle",
                "Example",
                1,
                "Verify page title"
            )
        );
    }

    @Test(description = "Verify page title displays correctly")
    public void testPageTitle() {
        // Navigate to your application
        navigateToApplication();

        // Create page object
        page = new ExamplePage(getDriver(), getConfiguration());

        // Get page title
        String title = page.getPageTitle();

        // Assert
        Assert.assertNotNull(title, "Page title should not be null");
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");

        logger.info("Test passed! Page title: {}", title);
    }
}
```

## Step 4: Run Your Test (1 minute)

### Build & Run

```bash
mvn clean test -Dtest=ExampleTest
```

### Expected Output

```
[INFO] Building Selenium Automation Testing Framework 1.0.0-RELEASE
[INFO] -------
[INFO] Running tests.example.ExampleTest
[INFO] Navigated to application: http://localhost:8080
[INFO] Test passed! Page title: Your Page Title
[INFO] Tests run: 1, Failures: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Common Test Commands

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=ExampleTest

# Run specific test method
mvn clean test -Dtest=ExampleTest#testPageTitle

# Run with Firefox browser
mvn clean test -Dbrowser=firefox

# Run with a different environment
mvn clean test -Denv=test

# Run with headless mode (for CI/CD)
mvn clean test -Dheadless=true

# Run in parallel with 4 threads
mvn clean test -DthreadCount=4

# Run with verbose logging
mvn clean test -X
```

## Framework Features You Now Have

### 1. Automatic Driver Management

```java
// Driver is automatically created in setUp()
// Get driver in your test
WebDriver driver = getDriver();

// Driver is automatically closed in tearDown()
```

### 2. Automatic Screenshot on Failure

```java
// Screenshots automatically captured when tests fail
// Look in: screenshots/ directory
```

### 3. Logging

```java
// Logging automatically configured
logger.info("This goes to logs/framework.log");
logger.error("Errors go to logs/errors.log");
```

### 4. Configuration by Environment

```bash
# Automatically loads dev.properties
mvn clean test

# Automatically loads staging.properties
mvn clean test -Denvironment=staging

# Automatically loads prod.properties
mvn clean test -Denvironment=prod
```

### 5. Wait Management

```java
// In your page objects, use waitManager
public String getTitle() {
    return getText(TITLE); // Automatically waits for visibility
}
```

## Page Object Quick Reference

### In Your Page Object

```java
public class MyPage extends BasePage {

    // 1. Define locators (private)
    private static final By USERNAME = By.id("username");
    private static final By SUBMIT = By.id("submit-btn");
    private static final By ERROR = By.className("error");

    // 2. Constructor
    public MyPage(WebDriver driver, Configuration config) {
        super(driver, config);
    }

    // 3. Page actions (public methods)
    public void enterUsername(String username) {
        sendKeys(USERNAME, username);
    }

    public void clickSubmit() {
        click(SUBMIT);
    }

    public String getErrorMessage() {
        return getText(ERROR);
    }

    public boolean isErrorDisplayed() {
        return isElementDisplayed(ERROR);
    }
}
```

### In Your Test

```java
@Test
public void testLogin() {
    MyPage page = new MyPage(getDriver(), getConfiguration());

    page.enterUsername("testuser");
    page.clickSubmit();

    Assert.assertTrue(page.isErrorDisplayed(), "Error should display");
}
```

## Debugging Tips

### 1. Check Logs

```bash
# Framework operations
tail -f logs/framework.log

# Test execution
tail -f logs/test-execution.log

# Selenium operations
tail -f logs/selenium.log

# Errors only
tail -f logs/errors.log
```

### 2. View Screenshots

```bash
# Automatically saved when tests fail
ls screenshots/
```

### 3. Enable Debug Logging

Edit `src/test/resources/logback.xml`:

```xml
<root level="DEBUG">  <!-- Changed from INFO -->
```

Then run test:

```bash
mvn clean test -X
```

### 4. Use Maven Failsafe Plugin

```bash
# Run only failed tests from previous run
mvn clean test -Dtest.failure.ignore=false
```

## Troubleshooting

### Issue: "WebDriver not initialized"

**Solution**: Ensure your test extends `BaseTest`

```java
public class MyTest extends BaseTest {  // Must extend BaseTest
    // ...
}
```

### Issue: "Element not found"

**Cause**: Wrong locator or page not fully loaded
**Solution**: Check locator using browser DevTools (F12)

```bash
# You can test locators in browser console
document.getElementById("username")  // For By.id("username")
document.querySelector(".error")     // For By.className("error")
```

### Issue: Tests timeout

**Cause**: Element not found within wait time
**Solution**: Increase timeout in properties:

```properties
wait.explicit.seconds=20  # Default is 10
```

### Issue: "Application URL not configured"

**Solution**: Set `baseUrl` in the environment properties file.

```properties
baseUrl=http://localhost:8080
```

### Issue: Tests pass locally but fail in CI/CD

**Solution**: Use headless mode in CI.

```bash
mvn clean test -Dheadless=true -Denv=prod
```

## Next Steps

1. **Read the documentation**
   - `README.md` - Full feature overview
   - `ARCHITECTURE.md` - Design patterns explanation
   - `ADDING_NEW_SCREENS.md` - Detailed guide for adding tests

2. **Create page objects for your application**
   - Create folders in `tests/` for each screen
   - Define locators in page objects
   - Expose actions (click, fill, submit)

3. **Write test cases**
   - Create test classes extending `BaseTest`
   - Use page objects for interactions
   - Add assertions for verification

4. **Integrate with CI/CD**
   - Add `.gitlab-ci.yml` or `.github/workflows/`
   - Run tests in headless mode
   - Archive reports and screenshots

## Example Project Structure

After adding your first feature:

```
tests/
├── base/
│   └── BaseTest.java
├── login/
│   ├── LoginTest.java
│   └── pages/
│       └── LoginPage.java
├── dashboard/
│   ├── DashboardTest.java
│   └── pages/
│       └── DashboardPage.java
└── example/
    ├── ExampleTest.java
    └── pages/
        └── ExamplePage.java
```

## Command Cheat Sheet

```bash
# Clean
mvn clean

# Build
mvn compile

# Run tests
mvn test

# Full build with tests
mvn clean test

# Skip tests
mvn clean install -DskipTests

# Single test class
mvn test -Dtest=LoginTest

# Single test method
mvn test -Dtest=LoginTest#testValidLogin

# Firefox instead of Chrome
mvn test -Dbrowser=firefox

# Staging environment
mvn test -Denvironment=staging

# Parallel execution (8 threads)
mvn test -DthreadCount=8

# Headless mode (for CI/CD)
mvn test -Dbrowser.headless=true

# Generate reports
mvn clean test && mvn site

# View test report
open target/surefire-reports/index.html  # macOS
start target\surefire-reports\index.html # Windows
```

## Success Checklist

- ✅ Installed Java 11 and Maven
- ✅ Downloaded/cloned framework
- ✅ Ran `mvn clean install`
- ✅ Set `baseUrl` in the environment properties
- ✅ Created example page object
- ✅ Created example test class
- ✅ Ran test with `mvn clean test`
- ✅ Test passed successfully
- ✅ Viewed screenshots in `screenshots/` folder
- ✅ Viewed logs in `logs/` folder

---

## Support

For more information:

- **Architecture Details**: See `ARCHITECTURE.md`
- **Adding New Screens**: See `ADDING_NEW_SCREENS.md`
- **Full Documentation**: See `README.md`
- **Code Comments**: Check JavaDoc in framework classes
- **Examples**: Review example code in `tests/login/`

---

**Happy Testing! 🚀**

Framework v1.0.0 | Created: August 31, 2026
