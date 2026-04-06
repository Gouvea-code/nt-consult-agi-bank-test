package br.com.agibank.qa.tests.performance;

import br.com.agibank.qa.config.TestConfig;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("performance")
class SearchPerformanceSmokeTest {

    @Test
    void shouldKeepSearchResponseWithinConfiguredBudget() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TestConfig.timeoutSeconds()))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        String searchUrl = TestConfig.baseUrl() + "?s=" + URLEncoder.encode(TestConfig.validSearchTerm(), StandardCharsets.UTF_8);
        int warmupRequests = TestConfig.performanceWarmupRequests();
        int measuredRequests = TestConfig.performanceMeasuredRequests();
        List<Long> responseTimes = new ArrayList<>();

        for (int index = 0; index < warmupRequests + measuredRequests; index++) {
            long start = System.nanoTime();
            HttpResponse<String> response = executeRequest(client, searchUrl);
            long elapsedMillis = Duration.ofNanos(System.nanoTime() - start).toMillis();

            assertEquals(200, response.statusCode(), "A busca deve responder com HTTP 200 durante o smoke de performance.");
            assertTrue(response.body().contains("Blog do Agi") || response.body().contains("Resultados encontrados"),
                    "A resposta da busca deve retornar conteúdo funcional.");

            if (index >= warmupRequests) {
                responseTimes.add(elapsedMillis);
            }

            Thread.sleep(TestConfig.performancePauseMillis());
        }

        long average = Math.round(responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
        long p95 = percentile(responseTimes, 95);
        writeReport(responseTimes, average, p95);

        assertAll(
                () -> assertFalse(responseTimes.isEmpty(), "O smoke precisa medir ao menos uma execução."),
                () -> assertTrue(average <= TestConfig.performanceAverageThresholdMs(),
                        String.format(Locale.ROOT, "Tempo médio acima do orçamento. Atual: %d ms.", average)),
                () -> assertTrue(p95 <= TestConfig.performanceP95ThresholdMs(),
                        String.format(Locale.ROOT, "P95 acima do orçamento. Atual: %d ms.", p95)));
    }

    private HttpResponse<String> executeRequest(HttpClient client, String searchUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(searchUrl))
                .timeout(Duration.ofSeconds(TestConfig.timeoutSeconds()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 429) {
            Thread.sleep(1500);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return response;
    }

    private long percentile(List<Long> values, int percentile) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        index = Math.max(index, 0);
        return sorted.get(index);
    }

    private void writeReport(List<Long> responseTimes, long average, long p95) throws IOException {
        Path reportDirectory = Paths.get("target", "performance");
        Files.createDirectories(reportDirectory);
        Path reportPath = reportDirectory.resolve("search-performance-smoke-report.json");

        String json = "{\n"
                + "  \"baseUrl\": \"" + TestConfig.baseUrl() + "\",\n"
                + "  \"searchTerm\": \"" + TestConfig.validSearchTerm() + "\",\n"
                + "  \"measuredRequests\": " + responseTimes.size() + ",\n"
                + "  \"averageMs\": " + average + ",\n"
                + "  \"p95Ms\": " + p95 + ",\n"
                + "  \"samplesMs\": " + responseTimes + "\n"
                + "}";

        Files.writeString(reportPath, json);
    }
}
