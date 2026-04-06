package br.com.agibank.qa.pages;

import br.com.agibank.qa.config.TestConfig;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BlogHomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchWidget = By.cssSelector(".ast-search-menu-icon.slide-search");
    private final By searchTrigger = By.cssSelector(".ast-search-icon .astra-search-icon");
    private final By searchInput = By.cssSelector("form.search-form input.search-field[name='s']");
    private final By pageBody = By.tagName("body");
    private final By cookieButtons = By.xpath("//button[contains(translate(normalize-space(.), 'ACEITROKPND', 'aceitrokpnd'), 'aceitar') or contains(translate(normalize-space(.), 'ACEITROKPND', 'aceitrokpnd'), 'aceito') or contains(translate(normalize-space(.), 'ACEITROKPND', 'aceitrokpnd'), 'ok') or contains(translate(normalize-space(.), 'ACEITROKPND', 'aceitrokpnd'), 'entendi') or contains(translate(normalize-space(.), 'ACEITROKPND', 'aceitrokpnd'), 'accept')]");

    public BlogHomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public BlogHomePage open() {
        driver.get(TestConfig.baseUrl());
        wait.until(ExpectedConditions.presenceOfElementLocated(searchTrigger));
        primeInteractiveScripts();
        return this;
    }

    public BlogHomePage dismissCookieBannerIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            List<WebElement> buttons = shortWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cookieButtons));
            buttons.stream().filter(WebElement::isDisplayed).findFirst().ifPresent(this::clickWithFallback);
        } catch (TimeoutException ignored) {
        }
        return this;
    }

    public BlogSearchResultsPage searchFor(String term) {
        expandSearchIfNeeded();

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), term, Keys.ENTER);

        return new BlogSearchResultsPage(driver, wait).waitUntilLoaded(term);
    }

    private void expandSearchIfNeeded() {
        if (isExpanded()) {
            return;
        }

        for (int attempt = 0; attempt < 2; attempt++) {
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(searchTrigger));
            clickWithFallback(trigger);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(4)).until(ExpectedConditions.or(
                        ExpectedConditions.attributeContains(searchWidget, "class", "ast-dropdown-active"),
                        ExpectedConditions.visibilityOfElementLocated(searchInput)));
                return;
            } catch (TimeoutException exception) {
                primeInteractiveScripts();
            }
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.attributeContains(searchWidget, "class", "ast-dropdown-active"),
                ExpectedConditions.visibilityOfElementLocated(searchInput)));
    }

    private boolean isExpanded() {
        List<WebElement> widgets = driver.findElements(searchWidget);
        return !widgets.isEmpty() && widgets.get(0).getDomAttribute("class").contains("ast-dropdown-active");
    }

    private void clickWithFallback(WebElement element) {
        try {
            element.click();
        } catch (Exception exception) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void primeInteractiveScripts() {
        try {
            WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(pageBody));
            new Actions(driver).moveToElement(body, 1, 1).pause(Duration.ofMillis(300)).perform();
            Thread.sleep(600);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Execução interrompida ao inicializar scripts do header.", interruptedException);
        } catch (Exception ignored) {
        }
    }
}
