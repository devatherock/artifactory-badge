package io.github.devatherock.artifactory.controllers

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

import javax.inject.Inject

import io.github.devatherock.artifactory.service.DockerBadgeService
import io.github.devatherock.test.TestUtil

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Shared
import spock.lang.Specification

/**
 * Test class for {@link VersionController}
 */
@MicronautTest(propertySources = 'classpath:application-test.yml')
class VersionControllerSpec extends Specification {

    @Shared
    WireMockServer mockServer = new WireMockServer(8081)

    @Inject
    @Client('/')
    HttpClient httpClient

    void setupSpec() {
        WireMock.configureFor(8081)
        mockServer.start()
    }

    void cleanupSpec() {
        mockServer.stop()
    }

    void cleanup() {
        mockServer.resetRequests()
    }

    void 'test get latest version badge - default label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.0', '2020-10-08T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.2', '2020-10-15T00:00:00.000-06:00'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/latest', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/abcdefgh', '2020-10-22T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('version'))
                .withQueryParam('message', equalTo('abcdefgh'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName).build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - custom label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.0', '2020-10-08T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.2', '2020-10-15T00:00:00.000-06:00'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/latest', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/abcdefgh', '2020-10-22T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('tag'))
                .withQueryParam('message', equalTo('abcdefgh'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName)
                        .queryParam('label', 'tag').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - caching'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.0', '2020-10-08T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/1.1.2', '2020-10-15T00:00:00.000-06:00'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/latest', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack/abcdefgh', '2020-10-22T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('dummy'))
                .withQueryParam('message', equalTo('abcdefgh'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName)
                        .queryParam('label', 'dummy').build()))
        String cachedBadge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName)
                        .queryParam('label', 'dummy').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
        cachedBadge == badge
    }

    void 'test get latest version badge - only semantic versions'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('dummy'))
                .withQueryParam('message', equalTo('v1.1.2'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName)
                        .queryParam('label', 'dummy')
                        .queryParam('sort', 'semver').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0")))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2")))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest")))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh")))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }
}
