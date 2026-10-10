package tests.pim;

import org.testng.Assert;
import org.testng.annotations.Test;

import framework.pages.LoginPage;
import framework.pages.PIMPage;
import framework.pages.SidebarMenu;
import tests.base.BaseTest;

/**
 * Smoke tests for the OrangeHRM PIM screen.
 */
public class PIMTest extends BaseTest {

    @Test(description = "Verify that the PIM screen opens after login")
    public void testPimScreenOpens() {
        login();

        PIMPage pimPage = new SidebarMenu(getDriver()).navigateToPIM();

        Assert.assertNotNull(pimPage, "PIM page should be created");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/pim"),
                "The PIM screen should be open");
    }

    private void login() {
        LoginPage loginPage = new LoginPage(getDriver(), getConfiguration());
        loginPage.navigate();
        loginPage.login(getTestUsername(), getTestPassword());
    }
}
