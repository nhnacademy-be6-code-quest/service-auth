package com.nhnacademy.auth.client;

import net.minidev.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "key", url = "https://api-keymanager.nhncloudservice.com/keymanager/v1.2/appkey/${key-manager.api.key}/secrets/")
public interface KeyManagerClient {
    @GetMapping("${secret.key.jwt}")
    JSONObject getJwtSecret(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.client.encoding}")
    JSONObject getClientEncoding(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.redis.db}")
    JSONObject getRedisDb(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.redis.host}")
    JSONObject getRedisHost(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.redis.password}")
    JSONObject getRedisPassword(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.redis.port}")
    JSONObject getRedisPort(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.rabbitmq.username}")
    JSONObject getRabbitmqUsername(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.rabbitmq.host}")
    JSONObject getRabbitmqHost(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.rabbitmq.password}")
    JSONObject getRabbitmqPassword(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.rabbitmq.port}")
    JSONObject getRabbitmqPort(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.payco.client.id}")
    JSONObject getPaycoClientId(@RequestHeader HttpHeaders headers);

    @GetMapping("${secret.key.payco.client.secret}")
    JSONObject getPaycoClientSecret(@RequestHeader HttpHeaders headers);
}
