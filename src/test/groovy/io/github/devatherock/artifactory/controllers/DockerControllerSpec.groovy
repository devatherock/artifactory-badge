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
 * Test class for {@link DockerController}
 */
abstract class DockerControllerSpec extends Specification {

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

    void 'test get image pull count - default label and custom badge'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(10))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(11))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(12))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(14))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('docker pulls'))
                .withQueryParam('message', equalTo('47'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.notFound()))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName).build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads/manifest.json?stats")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == TestUtil.getCustomBadgeResponse()
    }

    void 'test get image pull count - custom label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(10))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(20))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(30))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(40))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('downloads'))
                .withQueryParam('message', equalTo('100'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName)
                        .queryParam('label', 'downloads').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads/manifest.json?stats")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get image pull count - caching'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(
                        TestUtil.getFoldersResponse('/devatherock/simple-slack', '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(10))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(20))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(30))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats")
                .willReturn(WireMock.okJson(TestUtil.getManifestStats(40))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('dummy'))
                .withQueryParam('message', equalTo('100'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName)
                        .queryParam('label', 'dummy').build()))
        String cachedBadge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName)
                        .queryParam('label', 'dummy').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/latest/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/_uploads/manifest.json?stats")))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
        cachedBadge == badge
    }

    void 'test get image size - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('image size'))
                .withQueryParam('message', equalTo('11 MB'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName).build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get image size - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/1.2.0/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('size'))
                .withQueryParam('message', equalTo('11 MB'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName)
                        .queryParam('tag', '1.2.0')
                        .queryParam('label', 'size').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/1.2.0/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get image size - caching'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('dummy'))
                .withQueryParam('message', equalTo('11 MB'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName)
                        .queryParam('tag', 'latest')
                        .queryParam('label', 'dummy').build()))
        String cachedBadge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName)
                        .queryParam('tag', 'latest')
                        .queryParam('label', 'dummy').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
        cachedBadge == badge
    }

    void 'test get layers - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('layers'))
                .withQueryParam('message', equalTo('2'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName).build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get layers - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/1.2.0/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('number of layers'))
                .withQueryParam('message', equalTo('2'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName)
                        .queryParam('tag', '1.2.0')
                        .queryParam('label', 'number of layers').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/1.2.0/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
    }

    void 'test get layers - caching'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(TestUtil.getManifest())))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('dummy'))
                .withQueryParam('message', equalTo('2'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName)
                        .queryParam('tag', 'latest')
                        .queryParam('label', 'dummy').build()))
        String cachedBadge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName)
                        .queryParam('tag', 'latest')
                        .queryParam('label', 'dummy').build()))

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/static/v1")))
        badge == 'dummyBadge'
        cachedBadge == badge
    }
}
