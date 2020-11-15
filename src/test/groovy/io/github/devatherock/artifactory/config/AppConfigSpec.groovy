package io.github.devatherock.artifactory.config

import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link AppConfig}
 */
class AppConfigSpec extends Specification {
    @Subject
    AppConfig appConfig = new AppConfig()

    void 'test initialize http client'() {
        given:
        HttpClient httpClient = Mock()
        BlockingHttpClient outputClient = Mock()

        when:
        BlockingHttpClient blockingHttpClient = appConfig.httpClient(httpClient)

        then:
        httpClient.toBlocking() >> outputClient
        blockingHttpClient == outputClient
    }
}
