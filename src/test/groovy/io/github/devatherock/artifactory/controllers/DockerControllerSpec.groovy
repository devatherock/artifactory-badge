package io.github.devatherock.artifactory.controllers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Shared
import spock.lang.Specification

import javax.inject.Inject

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

/**
 * Test class for {@link DockerController}
 */
@MicronautTest(propertySources = 'classpath:application-test.yml')
class DockerControllerSpec extends Specification {

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

    void 'test get image pull count - default label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(getFoldersResponse())))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('1.1.0', 10))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('1.1.2', 20))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('latest', 30))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('abcdefgh', 40))))
        WireMock.givenThat(WireMock.get(WireMock.urlPathEqualTo('/static/v1'))
                .withQueryParam('label', equalTo('docker pulls'))
                .withQueryParam('message', equalTo('100'))
                .withQueryParam('color', equalTo('blue'))
                .willReturn(WireMock.okXml('dummyBadge')))

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName).build()))

        then:
        badge == 'dummyBadge'
    }

    void 'test get image pull count - custom label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(getFoldersResponse())))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('1.1.0', 10))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('1.1.2', 20))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('latest', 30))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh/manifest.json?stats")
                .willReturn(WireMock.okJson(getManifestStats('abcdefgh', 40))))
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
        badge == 'dummyBadge'
    }

    void 'test get image size - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(getManifest())))
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
        badge == 'dummyBadge'
    }

    void 'test get image size - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/1.2.0/manifest.json")
                .willReturn(WireMock.okJson(getManifest())))
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
        badge == 'dummyBadge'
    }

    void 'test get layers - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson(getManifest())))
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
        badge == 'dummyBadge'
    }

    void 'test get layers - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/1.2.0/manifest.json")
                .willReturn(WireMock.okJson(getManifest())))
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
        badge == 'dummyBadge'
    }

    String getFoldersResponse() {
        """{
            "repo": "docker",
            "path": "/devatherock/simple-slack",
            "created": "2018-09-23T18:02:56.147Z",
            "createdBy": "devatherock",
            "lastModified": "2018-09-23T18:02:56.147Z",
            "modifiedBy": "devatherock",
            "lastUpdated": "2018-09-23T18:02:56.147Z",
            "children": [
                {
                    "uri": "/1.1.0",
                    "folder": true
                },
                {
                    "uri": "/1.1.2",
                    "folder": true
                },
                {
                    "uri": "/latest",
                    "folder": true
                },
                {
                    "uri": "/abcdefgh",
                    "folder": true
                }
            ],
            "uri": "http://localhost:8081/artifactory/api/storage/docker/devatherock/simple-slack"
        }"""
    }

    String getManifestStats(String tag, int downloadCount) {
        """{
            "uri": "http://localhost:8081/artifactory/docker/devatherock/simple-slack/${tag}/manifest.json",
            "downloadCount": ${downloadCount},
            "lastDownloaded": 1602863958001,
            "lastDownloadedBy": "devatherock",
            "remoteDownloadCount": 0,
            "remoteLastDownloaded": 0
        }"""
    }

    String getManifest() {
        """{
            "schemaVersion": 2,
            "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
            "config": {
                "mediaType": "application/vnd.docker.container.image.v1+json",
                "size": 3027,
                "digest": "sha256:9fe1c24da9391a4d7346200a997c06c7c900466181081af7953a2a15c9fffd7c"
            },
            "layers": [
                {
                    "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                    "size": 10485760,
                    "digest": "sha256:e7c96db7181be991f19a9fb6975cdbbd73c65f4a2681348e63a141a2192a5f10"
                },
                {
                    "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                    "size": 1048576,
                    "digest": "sha256:f910a506b6cb1dbec766725d70356f695ae2bf2bea6224dbe8c7c6ad4f3664a2"
                }
            ]
        }"""
    }
}
