package tests.admin;

import org.testng.Assert;
import org.testng.annotations.Test;

import framework.pages.AdminPage;
import framework.pages.LoginPage;
import framework.pages.SidebarMenu;
import tests.base.BaseTest;

/**
 * Smoke tests for the OrangeHRM Admin screen.
 */
public class AdminTest extends BaseTest {

    @Test(description = "Verify that the Admin screen opens after login")
    public void testAdminScreenOpens() {
        login();

        AdminPage adminPage = new SidebarMenu(getDriver()).navigateToAdmin();

        Assert.assertNotNull(adminPage, "Admin page should be created");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/admin"),
                "The Admin screen should be open");
    }

    private void login() {
        LoginPage loginPage = new LoginPage(getDriver(), getConfiguration());
        loginPage.navigate();
        loginPage.login(getTestUsername(), getTestPassword());
    }
}
