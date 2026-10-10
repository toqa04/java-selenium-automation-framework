package tests.users;

import framework.config.Configuration;
import framework.pages.BasePage;
import framework.registry.ScreenRegistry;
import org.openqa.selenium.WebDriver;

/**
 * Example screen object used to prove screen registration independently of the
 * framework core.
 */
public class UsersPage extends BasePage {

    public static final String SCREEN_ID = "USERS";

    public UsersPage(WebDriver driver, Configuration configuration) {
        super(driver, configuration);
    }

    public static void registerScreen() {
        ScreenRegistry.registerScreen(SCREEN_ID,
                new ScreenRegistry.ScreenMetadata(
                        "Users",
                        "tests.users.UsersPage",
                        "/users",
                        "Users management screen example"
                )
        );
    }

    static {
        registerScreen();
    }

    public void navigate() {
        navigateTo("/users");
    }
}