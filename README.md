# Selenium Automation Framework

My first automation framework project built with Java, Selenium WebDriver, TestNG, and Maven. The project follows the Page Object Model and is organized for maintainability and future growth.

## Project Overview

This framework automates the OrangeHRM Demo application and currently includes tests for:

- Login
- Admin
- PIM
- Users

The goal is to build a structured Selenium automation foundation rather than tests for a single screen only.

## Features

- Page Object Model.
- Centralized WebDriver management using `DriverManager` and `ThreadLocal`.
- Support for Chrome, Firefox, Edge, and Safari.
- Automatic WebDriver setup using WebDriverManager.
- Centralized explicit waits through `WaitManager`.
- TestNG lifecycle management through `BaseTest`.
- Screenshots on test failure.
- Excel reports using Apache POI.
- Environment-based configuration.
- Logging with SLF4J and Logback.
- Test execution through Maven or the interactive `Main` runner.

## Project Structure

```text
src/
├── main/java/framework/
│   ├── config/       # Configuration loading and environments
│   ├── driver/       # DriverManager, Factory, BrowserType, DriverOptions
│   ├── exceptions/   # Framework exceptions
│   ├── execution/    # Test selection strategies
│   ├── excel/        # Excel reading and reporting
│   ├── pages/        # BasePage and application Page Objects
│   ├── registry/     # Screen and test registries
│   ├── reporting/    # Test result models and listeners
│   ├── screenshots/  # Screenshot management
│   ├── utils/        # General utilities
│   └── waits/        # Explicit wait utilities
│
└── test/
    ├── java/tests/
    │   ├── base/     # BaseTest
    │   ├── login/    # Login tests
    │   ├── admin/    # Admin tests
    │   ├── pim/      # PIM tests
    │   └── users/    # Users tests
    │
    └── resources/
        ├── config/   # Environment configuration files
        ├── test-data/# Test data files
        ├── testng/   # TestNG suite XML
        └── logback.xml
```

## Requirements

- Java 11.
- Maven 3.6 or newer.
- Chrome, Firefox, Edge, or Safari, depending on the selected configuration.
- An internet connection on the first run to download the required WebDriver.
- The environment is selected through the `-Denv` property, such as `dev`, `test`, `uat`, or `prod`.

## Running the Tests

Run the complete test suite from the project root:

```bash
mvn clean test
```

Run a specific test class:

```bash
mvn -Dtest=tests.users.UsersTest test
```

Run with a specific browser:

```bash
mvn clean test -Dbrowser=firefox
```

Run with a specific environment:

```bash
mvn clean test -Denv=test
```

Run the interactive `Main` runner:

```bash
mvn test-compile exec:java
```

Then select a test number from the menu, or choose `1` to run all discovered tests.

## Configuration

Safe shared defaults are committed in:

```text
src/test/resources/config/defaults.properties
```

Optional local files (`config.properties` and `config-{env}.properties`) can
override these defaults. They are excluded from Git so local credentials and
environment-specific settings are not published.

Provide demo credentials through environment variables or JVM properties:

```text
ORANGEHRM_TEST_USERNAME
ORANGEHRM_TEST_PASSWORD
```

The username defaults to `Admin`; a password must be supplied. For example,
on Windows PowerShell:

```powershell
$env:ORANGEHRM_TEST_USERNAME = "Admin"
$env:ORANGEHRM_TEST_PASSWORD = "<your demo password>"
mvn clean test
```

Alternatively, pass credentials as JVM properties:

```text
mvn clean test -Dtest.username=Admin -Dtest.password=<your-demo-password>
```

Configuration precedence is shared defaults, optional local base config,
optional environment config, then JVM system properties. Environment variables
are used for credentials when no JVM property is supplied.

## Test Credentials

Credentials for the demo application should not be committed to GitHub. Use environment variables or local configuration that is ignored by Git.

Recommended environment variables:

```text
ORANGEHRM_TEST_USERNAME
ORANGEHRM_TEST_PASSWORD
ORANGEHRM_BASE_URL
```

Never place passwords, tokens, or other secrets in public project files.

## Reports and Test Output

After execution, the framework generates:

```text
reports/excel/Test_<timestamp>.xlsx
reports/excel/screenshots/
test-output/
```

The Excel report contains:

- `Test`: Detailed results for every test.
- `Execution Summary`: Overall execution summary.
- `Failed Tests`: Failed tests only.

The detailed results sheet includes columns such as:

`Test ID`, `Screen`, `Test Case`, `Status`, `Start Time`, `End Time`, `Duration (ms)`, `Failure Reason`, `Exception Type`, and `Screenshot`.

## Important Notes

- The tests use the external OrangeHRM Demo application, so network issues or website changes may affect execution.
- Parallel execution is faster, but it may be less stable when testing a shared external website.
- This project is an initial, extensible automation framework foundation.
- Build artifacts, reports, and logs should not be committed to GitHub.

## Technology Stack

| Technology            | Version |
| --------------------- | ------: |
| Java                  |      11 |
| Selenium              |  4.35.0 |
| TestNG                |  7.10.2 |
| Maven Compiler Plugin |  3.13.0 |
| Maven Surefire Plugin |   3.3.1 |
| Apache POI            |   5.2.5 |
| WebDriverManager      |   5.9.3 |
| SLF4J                 |  2.0.13 |
| Logback               |   1.5.6 |

## Current Status

This is my first automation framework project, created to develop practical skills in:

- Designing maintainable automation frameworks.
- Writing organized Selenium tests.
- Managing WebDriver and explicit waits.
- Generating test reports and execution evidence.
- Running tests with Maven and TestNG.

## License

This is a personal educational project created for learning and development purposes.
