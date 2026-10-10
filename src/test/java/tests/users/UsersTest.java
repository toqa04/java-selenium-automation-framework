package tests.users;

import java.time.Duration;

import org.testng.Assert;
import org.testng.annotations.Test;

import framework.registry.ScreenRegistry;
import framework.utils.LoadingManager;
import framework.utils.UnexpectedUiHandler;

/**
 * Proof that a new screen can be added without modifying the framework core.
 */
public class UsersTest {

    @Test
    public void testUsersScreenIsRegistered() {
        UsersPage.registerScreen();

        ScreenRegistry.ScreenMetadata metadata = ScreenRegistry.getScreen(UsersPage.SCREEN_ID);
        Assert.assertNotNull(metadata, "Users screen should be registered in the registry.");
        Assert.assertEquals(metadata.getScreenName(), "Users");
        Assert.assertEquals(metadata.getUrlPattern(), "/users");
        Assert.assertTrue(metadata.getScreenClassName().contains("UsersPage"));
    }

    @Test
    public void testFrameworkUtilitiesHandleMissingDriverGracefully() {
        Assert.assertFalse(LoadingManager.isPageLoaded(null));
        Assert.assertFalse(LoadingManager.waitForPageReady(null, Duration.ofSeconds(1)));
        Assert.assertFalse(UnexpectedUiHandler.handleUnexpectedUi(null));
    }
}
