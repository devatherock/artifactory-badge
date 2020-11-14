package io.github.devatherock.artifactory.controllers

import io.github.devatherock.artifactory.service.DockerBadgeService
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.uri.UriBuilder
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

/**
 * Test class for {@link DockerController}
 */
@MicronautTest(propertySources = 'classpath:application-test.yml')
class DockerControllerSpec extends Specification {

    @Inject
    DockerBadgeService badgeService

    @Inject
    @Client('/')
    HttpClient httpClient

    void 'test get image pull count - default label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName).build()))

        then:
        1 * badgeService.getPullsCountBadge(packageName, 'docker pulls') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get image pull count - custom label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/pulls')
                        .queryParam('package', packageName)
                        .queryParam('label', 'downloads').build()))

        then:
        1 * badgeService.getPullsCountBadge(packageName, 'downloads') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get image size - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName).build()))

        then:
        1 * badgeService.getImageSizeBadge(packageName, 'latest', 'image size') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get image size - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/image-size')
                        .queryParam('package', packageName)
                        .queryParam('tag', '1.2.0')
                        .queryParam('label', 'size').build()))

        then:
        1 * badgeService.getImageSizeBadge(packageName, '1.2.0', 'size') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get layers - default label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName).build()))

        then:
        1 * badgeService.getImageLayersBadge(packageName, 'latest', 'layers') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get layers - custom label and tag'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/docker/layers')
                        .queryParam('package', packageName)
                        .queryParam('tag', '1.2.0')
                        .queryParam('label', 'number of layers').build()))

        then:
        1 * badgeService.getImageLayersBadge(packageName, '1.2.0', 'number of layers') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    @MockBean(DockerBadgeService)
    DockerBadgeService badgeService() {
        Mock(DockerBadgeService)
    }
}
