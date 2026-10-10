package framework.driver;

import framework.config.Configuration;

/**
 * Encapsulates WebDriver configuration options.
 *
 * This class holds common WebDriver options used during driver initialization.
 * It bridges the gap between Configuration and browser-specific options.
 *
 * @author Automation Team
 * @version 1.0.0
 */
public class DriverOptions {

    private final BrowserType browserType;
    private final boolean headless;
    private final boolean maximized;
    private final int implicitWaitSeconds;
    private final int pageLoadTimeoutSeconds;
    private final int scriptTimeoutSeconds;

    /**
     * Constructs DriverOptions from Configuration. This is the primary way to
     * create DriverOptions.
     *
     * @param config the framework configuration
     * @return configured DriverOptions
     */
    public static DriverOptions fromConfiguration(Configuration config) {
        BrowserType browser = BrowserType.fromString(config.getBrowser());
        return new DriverOptions(
                browser,
                config.isHeadlessMode(),
                config.isMaximizeBrowser(),
                config.getImplicitWaitSeconds(),
                config.getPageLoadTimeoutSeconds(),
                config.getScriptTimeout()
        );
    }

    /**
     * Constructs DriverOptions with all parameters.
     *
     * @param browserType the browser to use
     * @param headless whether to run in headless mode
     * @param maximized whether to maximize the browser window
     * @param implicitWaitSeconds implicit wait timeout
     * @param pageLoadTimeoutSeconds page load timeout
     * @param scriptTimeoutSeconds script execution timeout
     */
    public DriverOptions(BrowserType browserType, boolean headless, boolean maximized,
            int implicitWaitSeconds, int pageLoadTimeoutSeconds, int scriptTimeoutSeconds) {
        this.browserType = browserType;
        this.headless = headless;
        this.maximized = maximized;
        this.implicitWaitSeconds = implicitWaitSeconds;
        this.pageLoadTimeoutSeconds = pageLoadTimeoutSeconds;
        this.scriptTimeoutSeconds = scriptTimeoutSeconds;
    }

    public BrowserType getBrowserType() {
        return browserType;
    }

    public boolean isHeadless() {
        return headless;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public int getImplicitWaitSeconds() {
        return implicitWaitSeconds;
    }

    public int getPageLoadTimeoutSeconds() {
        return pageLoadTimeoutSeconds;
    }

    public int getScriptTimeoutSeconds() {
        return scriptTimeoutSeconds;
    }

    @Override
    public String toString() {
        return "DriverOptions{"
                + "browserType=" + browserType.getDisplayName()
                + ", headless=" + headless
                + ", maximized=" + maximized
                + ", implicitWaitSeconds=" + implicitWaitSeconds
                + ", pageLoadTimeoutSeconds=" + pageLoadTimeoutSeconds
                + ", scriptTimeoutSeconds=" + scriptTimeoutSeconds
                + '}';
    }
}
