package br.com.agibank.qa.config;

public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://blog.agibank.com.br/";
    private static final String DEFAULT_API_BASE_URL = DEFAULT_BASE_URL + "wp-json/wp/v2";

    private TestConfig() {
    }

    public static String baseUrl() {
        return normalizeUrl(readString("baseUrl", DEFAULT_BASE_URL));
    }

    public static String apiBaseUrl() {
        return normalizeUrl(readString("apiBaseUrl", DEFAULT_API_BASE_URL), false);
    }

    public static String browser() {
        return readString("browser", "chrome");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(readString("headless", "true"));
    }

    public static String chromeBinary() {
        String envValue = System.getenv("CHROME_BIN");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return readString("chrome.binary", "");
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(readString("timeout.seconds", "20"));
    }

    public static String validSearchTerm() {
        return readString("valid.search.term", "emprestimo");
    }

    public static String invalidSearchTerm() {
        return readString("invalid.search.term", "termoinexistenteagixpto");
    }

    public static int performanceWarmupRequests() {
        return Integer.parseInt(readString("performance.warmup.requests", "2"));
    }

    public static int performanceMeasuredRequests() {
        return Integer.parseInt(readString("performance.measured.requests", "8"));
    }

    public static long performancePauseMillis() {
        return Long.parseLong(readString("performance.pause.millis", "250"));
    }

    public static long performanceP95ThresholdMs() {
        return Long.parseLong(readString("performance.p95.threshold.ms", "4000"));
    }

    public static long performanceAverageThresholdMs() {
        return Long.parseLong(readString("performance.avg.threshold.ms", "2500"));
    }

    public static String screenshotDirectory() {
        return readString("screenshot.dir", "target/screenshots");
    }

    private static String readString(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key.replace('.', '_').toUpperCase());
        }
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String normalizeUrl(String url) {
        return normalizeUrl(url, true);
    }

    private static String normalizeUrl(String url, boolean enforceTrailingSlash) {
        String normalized = url.trim();
        if (enforceTrailingSlash && !normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        if (!enforceTrailingSlash && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
