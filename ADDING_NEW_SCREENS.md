# Adding New Test Screens/Pages

This document describes how to add new screens and test cases to the automation framework without modifying framework classes.

## Adding a New Screen

### Step 1: Create Page Object Class

Create a new class in `src/test/java/tests/[screen_name]/pages/`:

```java
package tests.dashboard.pages;

import framework.config.Configuration;
import framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    // Define locators as private static final By
    private static final By PAGE_TITLE = By.id("page-title");
    private static final By LOGOUT_BUTTON = By.cssSelector("[data-test='logout']");
    private static final By USER_PROFILE = By.className("user-profile");

    public DashboardPage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public void clickLogout() {
        click(LOGOUT_BUTTON);
    }

    public boolean isUserProfileDisplayed() {
        return isDisplayed(USER_PROFILE);
    }
}
```

### Step 2: Create Test Class

Create a new test class in `src/test/java/tests/[screen_name]/`:

```java
package tests.dashboard;

import framework.registry.TestRegistry;
import tests.base.BaseTest;
import tests.dashboard.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeClass
    public void registerTests() {
        // Register this test in the registry
        TestRegistry.registerTest("DASH_001",
            new TestRegistry.TestMetadata(
                "DASH_001",
                "tests.dashboard.DashboardTest",
                "testDashboardPageTitle",
                "Dashboard",
                1,
                "Verify dashboard page title"
            )
        );
    }

    @Test(description = "Verify dashboard page title")
    public void testDashboardPageTitle() {
        dashboardPage = new DashboardPage(getDriver(), getConfiguration());
        String title = dashboardPage.getPageTitle();
        Assert.assertEquals(title, "Dashboard", "Page title should be Dashboard");
    }
}
```

### Step 3: Add Test to src/test/resources/testng/testng.xml (Optional)

Update `src/test/resources/testng/testng.xml` to include your new test class:

```xml
<test name="Dashboard Tests">
    <classes>
        <class name="tests.dashboard.DashboardTest"/>
    </classes>
</test>
```

### Step 4: Run Your Test

```bash
mvn clean test -Dtest=DashboardTest
```

## Best Practices

### Locator Naming

Use descriptive names for locators that indicate the element's purpose:

- `SUBMIT_BUTTON` instead of `BUTTON_1`
- `ERROR_MESSAGE` instead of `MSG`
- `USER_NAME_FIELD` instead of `FIELD_2`

### Page Object Design

- **Private Locators**: Keep By locators private
- **Public Actions**: Expose only user actions (click, fill, submit)
- **Meaningful Methods**: Method names should describe actions (not `element1()`, use `clickSubmit()`)
- **Assertions in Tests**: Put assertions in test classes, not page objects

### Test Organization

```
tests/
├── login/
│   ├── pages/
│   │   └── LoginPage.java
│   └── LoginPageTest.java
├── dashboard/
│   ├── pages/
│   │   └── DashboardPage.java
│   └── DashboardTest.java
└── base/
    └── BaseTest.java
```

### Example: Complete Screen Implementation

```java
// Page Object
public class ProductSearchPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("search");
    private static final By SEARCH_BUTTON = By.id("search-btn");
    private static final By PRODUCT_LIST = By.xpath("//div[@class='product-item']");
    private static final By NO_RESULTS_MESSAGE = By.className("no-results");

    public ProductSearchPage(WebDriver driver, Configuration config) {
        super(driver, config);
    }

    public void searchForProduct(String productName) {
        type(SEARCH_INPUT, productName);
        click(SEARCH_BUTTON);
    }

    public List<WebElement> getSearchResults() {
        return waitManager.waitForPresenceOfAll(PRODUCT_LIST);
    }

    public boolean isNoResultsMessageDisplayed() {
        return isDisplayed(NO_RESULTS_MESSAGE);
    }
}

// Test Class
public class ProductSearchTest extends BaseTest {

    @Test
    public void testSearchProducts() {
        ProductSearchPage page = new ProductSearchPage(getDriver(), getConfiguration());
        page.navigateTo("https://example.com/products");

        page.searchForProduct("laptop");

        List<WebElement> results = page.getSearchResults();
        Assert.assertTrue(results.size() > 0, "Should find products");
    }

    @Test
    public void testSearchNoResults() {
        ProductSearchPage page = new ProductSearchPage(getDriver(), getConfiguration());
        page.navigate();

        page.searchForProduct("xyz-non-existent-product");

        Assert.assertTrue(page.isNoResultsMessageDisplayed(),
                         "Should show no results message");
    }
}
```

## Advanced Features

### Using Page Actions for Complex Interactions

```java
import framework.pages.PageActions;

public class ShoppingCartPage extends BasePage {

    private static final By QUANTITY_DROPDOWN = By.name("quantity");
    private static final By ITEM_CHECKBOX = By.className("item-select");

    public void setItemQuantity(int quantity) {
        WebElement dropdown = waitManager.waitForVisibility(QUANTITY_DROPDOWN);
        PageActions.selectByIndex(dropdown, quantity - 1);
    }

    public void toggleItemSelection() {
        WebElement checkbox = waitManager.waitForClickability(ITEM_CHECKBOX);
        PageActions.setCheckboxState(checkbox, true);
    }
}
```

### Using Excel Test Data

```java
public class DataDrivenTest extends BaseTest {

    @Test(dataProvider = "testData")
    public void testWithExcelData(String username, String password) {
        LoginPage page = new LoginPage(getDriver(), getConfiguration());
        page.login(username, password);
        // assertions...
    }

    @DataProvider(name = "testData")
    public Object[][] getTestData() {
        ExcelReader reader = new ExcelReader("src/test/resources/test-data/login-data.xlsx");
        List<Map<String, String>> data = reader.readSheet("LoginTest");

        Object[][] testData = new Object[data.size()][2];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i).get("username");
            testData[i][1] = data.get(i).get("password");
        }
        return testData;
    }
}
```

### Running Tests by Category

```bash
# Run only authentication tests
mvn clean test -Dcategory=Authentication

# Run high priority tests (priority 1-2)
mvn clean test -Dpriority=1
```

## Troubleshooting

### Common Issues

1. **Element Not Found**
   - Verify locator is correct
   - Check page is fully loaded (use wait conditions)
   - Use waitForVisibility/waitForClickability

2. **Test Fails on CI/CD**
   - Enable headless mode: `-Dheadless=true`
   - Increase wait timeouts for slower CI environments
   - Check screenshot for actual state

3. **Driver Not Initialized**
   - Ensure test extends BaseTest
   - BaseTest's setUp() method initializes driver
   - Check no exception in @BeforeMethod

## Summary

**To add a new screen:**

1. Create page object in `tests/[screen_name]/pages/`
2. Create test class in `tests/[screen_name]/`
3. Run test: `mvn clean test -Dtest=YourTestClass`

**No framework modification needed!**

## Interactive Runner

When a new test class whose name ends with `Test` and contains a TestNG `@Test` method is added, the runner will detect it automatically after `test-compile` is executed.

When the runner starts, it shows the following options:

1. Run all discovered screens.
2. Run one screen from the list.
3. Press `Y` and enter multiple screen numbers such as `2,4,5`.

After TestNG finishes, the project creates a new Excel file in `reports/excel` or in the custom path configured through `reportsDirectory`. The file includes `Execution Summary`, `Test Results`, and `Failed Tests`.

To run the interactive runner:

```bash
mvn test-compile exec:java
```

The default OrangeHRM credentials are `Admin / admin123`, and they can be changed in the environment property files instead of hardcoding them inside a page object or test class.
