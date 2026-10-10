package framework.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for OrangeHRM user administration.
 */
public class AdminPage {

    private final WebDriverWait wait;

    private final By addButton = By.xpath("//button[contains(normalize-space(), 'Add')]");
    private final By userRoleDropdown = By.xpath(
            "//label[normalize-space()='User Role']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]");
    private final By adminOption = By.xpath("//div[@role='listbox']//span[normalize-space()='Admin']");
    private final By employeeNameInput = By.xpath(
            "//label[normalize-space()='Employee Name']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By employeeAutocompleteOption = By.xpath("//div[@role='listbox']//span");
    private final By statusDropdown = By.xpath(
            "//label[normalize-space()='Status']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]");
    private final By enabledOption = By.xpath("//div[@role='listbox']//span[normalize-space()='Enabled']");
    private final By usernameInput = By.xpath(
            "//label[normalize-space()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By passwordInput = By.xpath(
            "//label[normalize-space()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By confirmPasswordInput = By.xpath(
            "//label[normalize-space()='Confirm Password']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By saveButton = By.xpath("//button[@type='submit']");
    private final By successToast = By.xpath("//div[contains(@class,'oxd-toast--success')]");

    public AdminPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddUser() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
    }

    public void selectUserRoleAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(adminOption)).click();
    }

    public void selectStatusEnabled() {
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(enabledOption)).click();
    }

    public void enterEmployeeName(String partialName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
        input.clear();
        input.sendKeys(partialName);
        wait.until(ExpectedConditions.elementToBeClickable(employeeAutocompleteOption)).click();
    }

    public void enterUserCredentials(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput)).sendKeys(password);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public boolean isSuccessToastDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successToast)).isDisplayed();
    }
}
