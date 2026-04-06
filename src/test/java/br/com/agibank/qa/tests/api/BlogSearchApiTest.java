package br.com.agibank.qa.tests.api;

import br.com.agibank.qa.config.TestConfig;
import br.com.agibank.qa.support.TextNormalizer;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("api")
class BlogSearchApiTest {

    private RequestSpecification specification;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = TestConfig.apiBaseUrl();
        specification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    void shouldReturnPostsWhenSearchingKnownKeyword() {
        String term = TestConfig.validSearchTerm();
        Response response = searchPosts(term);
        List<Map<String, Object>> posts = response.jsonPath().getList("$");

        assertAll(
                () -> assertEquals(200, response.statusCode(), "A API de busca deve responder com HTTP 200."),
                () -> assertFalse(posts.isEmpty(), "A API deve retornar posts para um termo conhecido."),
                () -> assertTrue(Integer.parseInt(response.getHeader("X-WP-Total")) > 0,
                        "O cabeçalho X-WP-Total deve informar quantidade positiva."),
                () -> assertTrue(posts.stream().allMatch(this::hasRequiredFields),
                        "Todos os itens devem possuir título e link."),
                () -> assertTrue(posts.stream()
                                .map(this::extractRenderedTitle)
                                .map(TextNormalizer::normalize)
                                .anyMatch(title -> title.contains(TextNormalizer.normalize(term))),
                        "Ao menos um post retornado deve ser aderente ao termo pesquisado."));
    }

    @Test
    void shouldReturnEmptyCollectionForUnknownKeyword() {
        Response response = searchPosts(TestConfig.invalidSearchTerm());
        List<Map<String, Object>> posts = response.jsonPath().getList("$");

        assertAll(
                () -> assertEquals(200, response.statusCode(), "A API deve responder com HTTP 200 para busca sem resultados."),
                () -> assertTrue(posts.isEmpty(), "A API deve retornar lista vazia para um termo inexistente."),
                () -> assertEquals("0", response.getHeader("X-WP-Total"),
                        "O cabeçalho X-WP-Total deve ser zero quando não há resultados."));
    }

    private Response searchPosts(String term) {
        Response response = RestAssured.given()
                .spec(specification)
                .queryParam("search", term)
                .queryParam("per_page", 5)
                .queryParam("_fields", "title,link,slug")
                .when()
                .get("/posts");

        if (response.statusCode() == 429) {
            sleep(1500);
            response = RestAssured.given()
                    .spec(specification)
                    .queryParam("search", term)
                    .queryParam("per_page", 5)
                    .queryParam("_fields", "title,link,slug")
                    .when()
                    .get("/posts");
        }

        return response;
    }

    private boolean hasRequiredFields(Map<String, Object> post) {
        return !extractRenderedTitle(post).isBlank() && post.get("link") != null;
    }

    @SuppressWarnings("unchecked")
    private String extractRenderedTitle(Map<String, Object> post) {
        Object titleObject = post.get("title");
        if (!(titleObject instanceof Map<?, ?> titleMap)) {
            return "";
        }
        Object rendered = ((Map<String, Object>) titleMap).get("rendered");
        return rendered == null ? "" : rendered.toString();
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Execução interrompida durante o retry da API.", interruptedException);
        }
    }
}
