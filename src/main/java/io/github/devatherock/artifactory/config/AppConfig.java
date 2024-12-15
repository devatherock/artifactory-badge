package io.github.devatherock.artifactory.config;

import java.util.concurrent.Executor;

import io.github.devatherock.artifactory.util.ParallelProcessor;

import io.micronaut.context.annotation.Factory;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Named;
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

    /**
     * @param appProperties miscellaneous application properties
     * @return a parallel processor bean
     */
    @Singleton
    public ParallelProcessor parallelProcessor(@Named("blocking") Executor executor, AppProperties appProperties) {
        return new ParallelProcessor(executor, appProperties);
    }
}
