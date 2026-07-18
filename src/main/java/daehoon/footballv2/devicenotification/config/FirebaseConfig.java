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

            byte[] decodedJson =
                    Base64.getDecoder().decode(serviceAccountBase64);

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


}
