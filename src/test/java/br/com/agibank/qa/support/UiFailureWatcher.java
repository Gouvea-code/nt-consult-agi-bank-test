package br.com.agibank.qa.support;

import br.com.agibank.qa.config.TestConfig;
import br.com.agibank.qa.core.DriverFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class UiFailureWatcher implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        WebDriver driver = DriverFactory.getCurrentDriver();
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }

        try {
            Path directory = Paths.get(TestConfig.screenshotDirectory());
            Files.createDirectories(directory);
            String fileName = sanitize(context.getRequiredTestClass().getSimpleName()
                    + "-"
                    + context.getRequiredTestMethod().getName()) + ".png";
            Path screenshotPath = directory.resolve(fileName);
            Files.write(screenshotPath, screenshotDriver.getScreenshotAs(OutputType.BYTES));
        } catch (IOException ignored) {
        }
    }

    private String sanitize(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_]", "-");
    }
}
