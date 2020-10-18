package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.Factory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.inject.Singleton;

@Factory
public class AppConfig {

    @Singleton
    public HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }
}
