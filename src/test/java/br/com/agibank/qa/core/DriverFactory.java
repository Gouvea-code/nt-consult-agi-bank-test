package br.com.agibank.qa.core;

import br.com.agibank.qa.config.TestConfig;
import java.time.Duration;
import java.util.Locale;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        if (CURRENT_DRIVER.get() != null) {
            return CURRENT_DRIVER.get();
        }

        WebDriver driver = switch (TestConfig.browser().toLowerCase(Locale.ROOT)) {
            case "edge" -> createEdgeDriver();
            default -> createChromeDriver();
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.timeoutSeconds() + 20L));
        driver.manage().window().setSize(new Dimension(1600, 1000));
        CURRENT_DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getCurrentDriver() {
        return CURRENT_DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = CURRENT_DRIVER.get();
        if (driver != null) {
            driver.quit();
            CURRENT_DRIVER.remove();
        }
    }

    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--window-size=1600,1000",
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--disable-gpu",
                "--disable-notifications",
                "--lang=pt-BR");

        if (TestConfig.headless()) {
            options.addArguments("--headless=new");
        }

        String binaryPath = TestConfig.chromeBinary();
        if (!binaryPath.isBlank()) {
            options.setBinary(binaryPath);
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--window-size=1600,1000", "--disable-notifications");
        if (TestConfig.headless()) {
            options.addArguments("--headless=new");
        }
        return new EdgeDriver(options);
    }
}
