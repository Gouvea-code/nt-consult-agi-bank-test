package br.com.agibank.qa.bdd.web.hooks;

import br.com.agibank.qa.config.TestConfig;
import br.com.agibank.qa.core.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class WebHooks {

    @Before("@web")
    public void setUp() {
        DriverFactory.createDriver();
    }

    @After("@web")
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getCurrentDriver();
        if (scenario.isFailed() && driver instanceof TakesScreenshot screenshotDriver) {
            attachScreenshot(scenario, screenshotDriver);
        }
        DriverFactory.quitDriver();
    }

    private void attachScreenshot(Scenario scenario, TakesScreenshot screenshotDriver) {
        byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "falha-web");

        try {
            Path directory = Paths.get(TestConfig.screenshotDirectory());
            Files.createDirectories(directory);
            Path filePath = directory.resolve(sanitize(scenario.getName()) + ".png");
            Files.write(filePath, screenshot);
        } catch (IOException ignored) {
        }
    }

    private String sanitize(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_]", "-");
    }
}
