package daehoon.footballv2.monitoring;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.Request;
import io.sentry.protocol.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class SentryMonitoringConfigurationTest {

    @Test
    void productionRequestBodySizeUsesAValidSentryValue() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application-prod.properties")) {
            assertThat(input).isNotNull();
            properties.load(input);
        }

        String configuredValue = properties.getProperty(
                "sentry.max-request-body-size");

        assertThat(SentryOptions.RequestSize.valueOf(
                configuredValue.toUpperCase(Locale.ROOT)))
                .isEqualTo(SentryOptions.RequestSize.NONE);
    }

    @Test
    void removesSensitiveRequestDataBeforeSending() {
        SentryMonitoringConfiguration configuration =
                new SentryMonitoringConfiguration();
        SentryOptions.BeforeSendCallback callback =
                configuration.scrubSensitiveSentryData();

        Request request = new Request();
        request.setUrl("https://example.com/api/members/me?token=secret");
        request.setQueryString("token=secret");
        request.setCookies("session=secret");
        request.setData(Map.of("password", "1234"));
        request.setHeaders(new LinkedHashMap<>(Map.of(
                "Authorization", "Bearer secret-token",
                "Cookie", "session=secret",
                "Content-Type", "application/json"
        )));

        SentryEvent event = new SentryEvent(new RuntimeException("test"));
        event.setRequest(request);
        event.setUser(new User());

        SentryEvent result = callback.execute(event, new Hint());

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isNull();
        assertThat(result.getRequest().getData()).isNull();
        assertThat(result.getRequest().getCookies()).isNull();
        assertThat(result.getRequest().getQueryString()).isNull();
        assertThat(result.getRequest().getUrl())
                .isEqualTo("https://example.com/api/members/me");
        assertThat(result.getRequest().getHeaders())
                .containsOnlyKeys("Content-Type");
    }
}
