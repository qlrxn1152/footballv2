package daehoon.footballv2.devicenotification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    @Value("${firebase.service-account-base64:}")
    private String serviceAccountBase64;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        GoogleCredentials credentials;

        if (serviceAccountBase64 != null
                && !serviceAccountBase64.isBlank()) {

            byte[] decodedJson = decodeServiceAccount(serviceAccountBase64);

            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(decodedJson)
            );
        } else {
            credentials = GoogleCredentials.getApplicationDefault();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK 초기화 완료: appName={}", app.getName());
            return app;
        }

        FirebaseApp app = FirebaseApp.getInstance();
        log.info("기존 Firebase Admin SDK 인스턴스 사용: appName={}", app.getName());
        return app;
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    static byte[] decodeServiceAccount(String configuredValue) {
        String normalized = configuredValue.trim();

        if (isWrappedInQuotes(normalized)) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }

        // Railway에 서비스 계정 JSON 원문을 넣은 경우도 허용합니다.
        if (normalized.startsWith("{")) {
            return normalized.getBytes(StandardCharsets.UTF_8);
        }

        // macOS base64 명령은 긴 값을 여러 줄로 출력할 수 있습니다.
        // 환경변수에 포함된 줄바꿈/공백을 제거한 뒤 디코딩합니다.
        String compactBase64 = normalized.replaceAll("\\s+", "");

        try {
            return Base64.getDecoder().decode(compactBase64);
        } catch (IllegalArgumentException standardBase64Exception) {
            try {
                return Base64.getUrlDecoder().decode(compactBase64);
            } catch (IllegalArgumentException urlBase64Exception) {
                throw new IllegalStateException(
                        "FIREBASE_SERVICE_ACCOUNT_BASE64 값이 JSON 또는 유효한 Base64 형식이 아닙니다.",
                        standardBase64Exception
                );
            }
        }
    }

    private static boolean isWrappedInQuotes(String value) {
        return value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")));
    }


}
