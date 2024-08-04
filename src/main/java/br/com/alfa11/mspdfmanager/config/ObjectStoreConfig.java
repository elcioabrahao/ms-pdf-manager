package br.com.alfa11.mspdfmanager.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectStoreConfig {

        @Value("${minio.url}")
        private String url;

        @Value("${minio.access.name}")
        private String accessKey;

        @Value("${minio.access.secret}")
        private String accessSecret;

        @Bean
        public MinioClient minioClient() {
            return MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, accessSecret)
                    .build();
        }
}
