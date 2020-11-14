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
 * Test class for {@link VersionController}
 */
@MicronautTest(propertySources = 'classpath:application-test.yml')
class VersionControllerSpec extends Specification {

    @Inject
    DockerBadgeService dockerBadgeService

    @Inject
    @Client('/')
    HttpClient httpClient

    void 'test get latest version badge - default label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName).build()))

        then:
        1 * dockerBadgeService.getLatestVersionBadge(packageName, 'version') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - custom label'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        when:
        String badge = httpClient.toBlocking().retrieve(
                HttpRequest.GET(UriBuilder.of('/version')
                        .queryParam('package', packageName)
                        .queryParam('label', 'tag').build()))

        then:
        1 * dockerBadgeService.getLatestVersionBadge(packageName, 'tag') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    @MockBean(DockerBadgeService)
    DockerBadgeService badgeService() {
        Mock(DockerBadgeService)
    }
}
