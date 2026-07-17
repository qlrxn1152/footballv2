package daehoon.footballv2.monitoring;

import io.sentry.SentryOptions;
import io.sentry.protocol.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Configuration
public class SentryMonitoringConfiguration {

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "cookie",
            "set-cookie",
            "x-api-key"
    );

    @Bean
    SentryOptions.BeforeSendCallback scrubSensitiveSentryData() {
        return (event, hint) -> {
            event.setUser(null);

            Request request = event.getRequest();
            if (request == null) {
                return event;
            }

            request.setData(null);
            request.setCookies(null);
            request.setQueryString(null);
            request.setUrl(removeQueryString(request.getUrl()));
            request.setHeaders(sanitizeHeaders(request.getHeaders()));
            return event;
        };
    }

    private Map<String, String> sanitizeHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return headers;
        }

        Map<String, String> sanitized = new LinkedHashMap<>(headers);
        sanitized.keySet().removeIf(header ->
                SENSITIVE_HEADERS.contains(header.toLowerCase(Locale.ROOT))
        );
        return sanitized;
    }

    private String removeQueryString(String url) {
        if (url == null) {
            return null;
        }
        int queryStart = url.indexOf('?');
        return queryStart < 0 ? url : url.substring(0, queryStart);
    }
}
