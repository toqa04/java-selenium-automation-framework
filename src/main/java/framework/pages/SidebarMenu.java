package framework.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared navigation menu displayed after OrangeHRM authentication.
 */
public class SidebarMenu {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pimMenu = By.xpath("//span[normalize-space()='PIM']");
    private final By adminMenu = By.xpath("//span[normalize-space()='Admin']");
    private final By leaveMenu = By.xpath("//span[normalize-space()='Leave']");

    public SidebarMenu(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public PIMPage navigateToPIM() {
        wait.until(ExpectedConditions.elementToBeClickable(pimMenu)).click();
        return new PIMPage(driver);
    }

    public AdminPage navigateToAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(adminMenu)).click();
        return new AdminPage(driver);
    }

    public void navigateToLeave() {
        wait.until(ExpectedConditions.elementToBeClickable(leaveMenu)).click();
    }
}
