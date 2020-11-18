package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Singleton;

/**
 * Bean definitions for the application
 */
@Factory
public class AppConfig {

    /**
     * HTTP client for interacting with artifactory APIs
     * 
     * @param artifactoryClient
     * @return a http client
     */
    @Singleton
    public BlockingHttpClient httpClient(@Client("${artifactory.url}") HttpClient artifactoryClient) {
        return artifactoryClient.toBlocking();
    }
}
