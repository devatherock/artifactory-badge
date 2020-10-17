package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;

import javax.inject.Singleton;

@Factory
public class AppConfig {

    @Singleton
    public BlockingHttpClient blockingHttpClient() {
        return HttpClient.create(null).toBlocking();
    }
}
