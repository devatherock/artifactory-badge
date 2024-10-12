package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;

/**
 * Bean definitions for the application
 */
@Factory
public class AppConfig {

    /**
     * HTTP client for interacting with HTTP APIs
     *
     * @param httpClient
     * @return a http client
     */
    @Singleton
    public BlockingHttpClient httpClient(@Client HttpClient httpClient) {
        return httpClient.toBlocking();
    }
}
