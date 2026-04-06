package br.com.agibank.qa.core;

import br.com.agibank.qa.config.TestConfig;
import br.com.agibank.qa.support.UiFailureWatcher;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseUiTest {

    @RegisterExtension
    static final UiFailureWatcher UI_FAILURE_WATCHER = new UiFailureWatcher();

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.timeoutSeconds()));
    }

    @AfterEach
    void tearDown() {
        DriverFactory.quitDriver();
    }
}
