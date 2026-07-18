package daehoon.footballv2.devicenotification.config;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseConfigTest {

    private static final String SERVICE_ACCOUNT_JSON =
            "{\"type\":\"service_account\",\"project_id\":\"footballv2\"}";

    @Test
    void decodesBase64ContainingLineBreaks() {
        String encoded = Base64.getEncoder()
                .encodeToString(SERVICE_ACCOUNT_JSON.getBytes(StandardCharsets.UTF_8));
        String wrapped = encoded.substring(0, encoded.length() / 2)
                + "\n"
                + encoded.substring(encoded.length() / 2);

        byte[] decoded = FirebaseConfig.decodeServiceAccount(wrapped);

        assertThat(new String(decoded, StandardCharsets.UTF_8))
                .isEqualTo(SERVICE_ACCOUNT_JSON);
    }

    @Test
    void acceptsRawJson() {
        byte[] decoded = FirebaseConfig.decodeServiceAccount(SERVICE_ACCOUNT_JSON);

        assertThat(new String(decoded, StandardCharsets.UTF_8))
                .isEqualTo(SERVICE_ACCOUNT_JSON);
    }

    @Test
    void acceptsQuotedBase64() {
        String encoded = Base64.getEncoder()
                .encodeToString(SERVICE_ACCOUNT_JSON.getBytes(StandardCharsets.UTF_8));

        byte[] decoded = FirebaseConfig.decodeServiceAccount("\"" + encoded + "\"");

        assertThat(new String(decoded, StandardCharsets.UTF_8))
                .isEqualTo(SERVICE_ACCOUNT_JSON);
    }
}
