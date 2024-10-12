package io.github.devatherock.artifactory.controllers

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

import io.github.devatherock.artifactory.service.DockerBadgeService
import io.github.devatherock.test.TestUtil

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test that calls remote endpoints. Needed for proper netty SSL reflection config
 */
abstract class RemoteUrlsSpec extends Specification {

    @Shared
    WireMockServer mockServer = new WireMockServer(8081)

    @Inject
    @Client('${test.server.url}')
    HttpClient httpClient

    void setupSpec() {
        WireMock.configureFor(8081)
        mockServer.start()
    }

    void cleanupSpec() {
        mockServer.stop()
    }

    void cleanup() {
        mockServer.resetAll()
    }

    void 'test get image size'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName).build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        badge.startsWith('<svg')
        badge.contains('MB')
        badge.endsWith('</svg>')
    }
}
