package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Singleton;

@Factory
public class AppConfig {

    @Singleton
    public BlockingHttpClient httpClient(@Client("${artifactory.url}") HttpClient artifactoryClient) {
        return artifactoryClient.toBlocking();
    }
}
