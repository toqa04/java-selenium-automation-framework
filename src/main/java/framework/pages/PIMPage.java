package framework.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object for OrangeHRM's PIM employee management flow.
 */
public class PIMPage {

    private final WebDriverWait wait;

    private final By addEmployeeTab = By.xpath("//a[normalize-space()='Add Employee']");
    private final By firstNameInput = By.name("firstName");
    private final By lastNameInput = By.name("lastName");
    private final By employeeIdInput = By.xpath(
            "//label[normalize-space()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By saveButton = By.xpath("//button[@type='submit']");
    private final By personalDetailsHeader = By.xpath("//h6[normalize-space()='Personal Details']");

    public PIMPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddEmployeeTab() {
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeTab)).click();
    }

    public void enterEmployeeDetails(String firstName, String lastName, String employeeId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput)).sendKeys(lastName);

        WebElement idField = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeIdInput));
        idField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        idField.sendKeys(employeeId);
    }

    public void clickSave() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public boolean isPersonalDetailsDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(personalDetailsHeader)).isDisplayed();
    }
}
