package br.com.agibank.qa.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BlogSearchResultsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By heading = By.cssSelector("h1.page-title, .page-title.ast-archive-title");
    private final By resultTitles = By.cssSelector("article .entry-title a");
    private final By noResultsContainer = By.cssSelector(".no-results");
    private final By searchInput = By.cssSelector("form.search-form input.search-field[name='s']");
    private final By body = By.tagName("body");

    public BlogSearchResultsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public BlogSearchResultsPage waitUntilLoaded(String term) {
        wait.until(driver -> driver.getCurrentUrl().contains("?s="));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(resultTitles),
                ExpectedConditions.presenceOfElementLocated(noResultsContainer),
                ExpectedConditions.presenceOfElementLocated(heading)));
        wait.until(ExpectedConditions.attributeContains(searchInput, "value", term));
        return this;
    }

    public String getHeadingText() {
        List<org.openqa.selenium.WebElement> headings = driver.findElements(heading);
        return headings.isEmpty() ? "" : headings.get(0).getText().trim();
    }

    public List<String> getResultTitles() {
        return driver.findElements(resultTitles)
                .stream()
                .map(this::extractText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public boolean hasResults() {
        return !getResultTitles().isEmpty();
    }

    public boolean isEmptyStateDisplayed() {
        return !driver.findElements(noResultsContainer).isEmpty() || getBodyClasses().contains("search-no-results");
    }

    public String getDisplayedSearchTerm() {
        return driver.findElement(searchInput).getDomProperty("value").trim();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getBodyClasses() {
        return driver.findElement(body).getDomAttribute("class");
    }

    private String extractText(WebElement element) {
        String text = element.getText();
        if (text == null || text.isBlank()) {
            text = element.getDomProperty("textContent");
        }
        return text == null ? "" : text.trim();
    }
}
