package com.example.todolist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestClientConfig {

  @Bean
  public ClientHttpRequestFactory externalClientHttpRequestFactory(
      @Value("${app.external.connect-timeout-ms:2000}") int connectTimeoutMs,
      @Value("${app.external.read-timeout-ms:3000}") int readTimeoutMs) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(connectTimeoutMs);
    factory.setReadTimeout(readTimeoutMs);
    return factory;
  }
}
