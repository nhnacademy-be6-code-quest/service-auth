package com.nhnacademy.auth.config;

import com.nhnacademy.auth.client.KeyManagerClient;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeyManagerConfig {
    private final KeyManagerClient keyManagerClient;
    @Value("${user.access.key.id}")
    private String accessKeyId;
    @Value("${secret.access.key}")
    private String accessKeySecret;

    @Bean
    public SecretKey jwtSecretKey() {
        String jwtSecretKey = getKey(keyManagerClient.getJwtSecret(getAccessHeaders()));
        log.info("JWT Secret Key: {}", jwtSecretKey);
        return new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    @Bean
    public String clientEncodingKey() {
        String clientEncoding = getKey(keyManagerClient.getClientEncoding(getAccessHeaders()));
        log.info("Client Encoding Key: {}", clientEncoding);
        return clientEncoding;
    }

    @Bean
    public String redisHost() {
        String redisHost = getKey(keyManagerClient.getRedisHost(getAccessHeaders()));
        log.info("Redis Host Key: {}", redisHost);
        return redisHost;
    }

    @Bean
    public String redisPassword() {
        String redisPassword = getKey(keyManagerClient.getRedisPassword(getAccessHeaders()));
        log.info("Redis Password Key: {}", redisPassword);
        return redisPassword;
    }

    @Bean
    public Integer redisPort() {
        String redisPort = getKey(keyManagerClient.getRedisPort(getAccessHeaders()));
        log.info("Redis Port Key: {}", redisPort);
        return Integer.parseInt(redisPort);
    }

    @Bean
    public Integer redisDb() {
        String redisDb = getKey(keyManagerClient.getRedisDb(getAccessHeaders()));
        log.info("Redis Db Key: {}", redisDb);
        return Integer.parseInt(redisDb);
    }

    @Bean
    public String rabbitHost() {
        String rabbitmqHost = getKey(keyManagerClient.getRabbitmqHost(getAccessHeaders()));
        log.info("Rabbitmq Host Key: {}", rabbitmqHost);
        return rabbitmqHost;
    }

    @Bean
    public String rabbitPassword() {
        String rabbitmqPassword = getKey(keyManagerClient.getRabbitmqPassword(getAccessHeaders()));
        log.info("Rabbitmq Password Key: {}", rabbitmqPassword);
        return rabbitmqPassword;
    }

    @Bean
    public String rabbitUsername() {
        String rabbitmqUsername = getKey(keyManagerClient.getRabbitmqUsername(getAccessHeaders()));
        log.info("Rabbitmq Username: {}", rabbitmqUsername);
        return rabbitmqUsername;
    }

    @Bean
    public Integer rabbitPort() {
        String rabbitmqPort = getKey(keyManagerClient.getRabbitmqPort(getAccessHeaders()));
        log.info("Rabbitmq Port Key: {}", rabbitmqPort);
        return Integer.parseInt(rabbitmqPort);
    }

    @Bean
    public String paycoClientId() {
        String paycoClientId = getKey(keyManagerClient.getPaycoClientId(getAccessHeaders()));
        log.info("Payco Client Id Key: {}", paycoClientId);
        return paycoClientId;
    }

    @Bean
    public String paycoClientSecret() {
        String paycoClientSecret = getKey(keyManagerClient.getPaycoClientSecret(getAccessHeaders()));
        log.info("Payco Client Secret Key: {}", paycoClientSecret);
        return paycoClientSecret;
    }

    private HttpHeaders getAccessHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-TC-AUTHENTICATION-ID", accessKeyId);
        headers.add("X-TC-AUTHENTICATION-SECRET", accessKeySecret);
        return headers;
    }

    private String getKey(JSONObject jsonObject) {
        Map<String, Object> responseMap = jsonObject;
        Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
        return (String) bodyMap.get("secret");
    }
}
