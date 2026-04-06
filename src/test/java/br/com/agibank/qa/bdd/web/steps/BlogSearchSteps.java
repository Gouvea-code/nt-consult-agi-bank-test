package br.com.agibank.qa.bdd.web.steps;

import br.com.agibank.qa.config.TestConfig;
import br.com.agibank.qa.core.DriverFactory;
import br.com.agibank.qa.pages.BlogHomePage;
import br.com.agibank.qa.pages.BlogSearchResultsPage;
import br.com.agibank.qa.support.TextNormalizer;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BlogSearchSteps {

    private WebDriver driver;
    private WebDriverWait wait;
    private BlogHomePage homePage;
    private BlogSearchResultsPage resultsPage;
    private String searchedTerm;

    @Dado("que acesso a home do Blog do Agi")
    public void queAcessoAHomeDoBlogDoAgi() {
        driver = DriverFactory.getCurrentDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.timeoutSeconds()));
        homePage = new BlogHomePage(driver, wait)
                .open()
                .dismissCookieBannerIfPresent();
    }

    @Quando("pesquiso pela lupa do cabeçalho pelo termo {string}")
    public void pesquisoPelaLupaDoCabecalhoPeloTermo(String term) {
        searchedTerm = term;
        resultsPage = homePage.searchFor(term);
    }

    @Entao("devo ser redirecionado para a página de resultados da busca pelo termo {string}")
    public void devoSerRedirecionadoParaAPaginaDeResultadosDaBuscaPeloTermo(String expectedTerm) {
        String normalizedTerm = TextNormalizer.normalize(expectedTerm);

        Assertions.assertAll(
                () -> Assertions.assertTrue(resultsPage.getCurrentUrl().contains("?s=" + expectedTerm),
                        "A URL da busca deve preservar o termo pesquisado."),
                () -> Assertions.assertTrue(TextNormalizer.normalize(resultsPage.getHeadingText()).contains(normalizedTerm),
                        "O cabeçalho da página deve indicar o termo pesquisado."));
    }

    @Entao("devo visualizar artigos relacionados ao termo pesquisado")
    public void devoVisualizarArtigosRelacionadosAoTermoPesquisado() {
        List<String> resultTitles = resultsPage.getResultTitles();
        String normalizedTerm = TextNormalizer.normalize(searchedTerm);

        Assertions.assertAll(
                () -> Assertions.assertFalse(resultTitles.isEmpty(), "A busca positiva deve retornar artigos."),
                () -> Assertions.assertTrue(resultTitles.stream()
                                .map(TextNormalizer::normalize)
                                .anyMatch(title -> title.contains(normalizedTerm)),
                        "Ao menos um artigo retornado deve ser aderente ao termo pesquisado."));
    }

    @Entao("o campo de busca deve permanecer preenchido com o termo {string}")
    public void oCampoDeBuscaDevePermanecerPreenchidoComOTermo(String expectedTerm) {
        Assertions.assertEquals(expectedTerm, resultsPage.getDisplayedSearchTerm(),
                "O campo de busca deve manter o termo pesquisado após a submissão.");
    }

    @Entao("devo visualizar o estado vazio da busca")
    public void devoVisualizarOEstadoVazioDaBusca() {
        Assertions.assertTrue(resultsPage.isEmptyStateDisplayed(),
                "A busca com termo inexistente deve exibir o estado vazio da página.");
    }

    @Entao("não devo visualizar artigos listados para a pesquisa")
    public void naoDevoVisualizarArtigosListadosParaAPesquisa() {
        Assertions.assertFalse(resultsPage.hasResults(),
                "A busca com termo inexistente não deve exibir artigos na listagem.");
    }
}
