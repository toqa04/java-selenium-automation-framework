package tests.login;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import framework.pages.DashboardPage;
import framework.pages.LoginPage;
import framework.registry.TestRegistry;
import tests.base.BaseTest;

/**
 * OrangeHRM login flow: login and verify that the Dashboard is displayed.
 */
public class LoginPageTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeClass
    public void registerTests() {
        logger.info("Registering OrangeHRM login tests...");

        TestRegistry.registerTest("ORANGEHRM_LOGIN_001",
                new TestRegistry.TestMetadata(
                        "ORANGEHRM_LOGIN_001",
                        "tests.login.LoginPageTest",
                        "testSuccessfulLogin",
                        "Authentication",
                        1,
                        "Verify OrangeHRM accepts valid credentials and opens Dashboard"
                )
        );
    }

    @Test(description = "Verify OrangeHRM login opens Dashboard", priority = 1)
    public void testSuccessfulLogin() {
        logger.info("Starting OrangeHRM login test");

        loginPage = new LoginPage(getDriver(), getConfiguration());
        loginPage.navigate();

        loginPage.login(getTestUsername(), getTestPassword());

        DashboardPage dashboard = new DashboardPage(getDriver(), getConfiguration());
        Assert.assertTrue(dashboard.isDisplayed(), "Dashboard should be displayed after login.");
        Assert.assertEquals(dashboard.getHeading(), "Dashboard");

        logger.info("OrangeHRM login test passed");
    }
}
