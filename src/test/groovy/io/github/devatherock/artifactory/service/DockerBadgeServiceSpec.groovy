package io.github.devatherock.artifactory.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.devatherock.artifactory.config.ArtifactoryProperties
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo
import io.github.devatherock.artifactory.util.BadgeGenerator
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

/**
 * Test class for {@link DockerBadgeService}
 */
class DockerBadgeServiceSpec extends Specification {
    @Subject
    DockerBadgeService dockerBadgeService

    @Shared
    WireMockServer mockServer = new WireMockServer(8081)

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

    BlockingHttpClient httpClient = HttpClient.create(new URL('http://localhost:8081')).toBlocking()
    BadgeGenerator badgeGenerator = Mock()
    ArtifactoryProperties config = new ArtifactoryProperties(url: 'http://localhost:8081', apiKey: 'dummyKey')

    void setup() {
        config.init()
        dockerBadgeService = new DockerBadgeService(httpClient, badgeGenerator, config)
    }

    void 'test get image size badge - manifest not found'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getImageSizeBadge(packageName, 'latest', 'image size')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('image size', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get version badge value'() {
        expect:
        dockerBadgeService.getVersionBadgeValue(new ArtifactoryFolderInfo(path: path)) == outputVersion

        where:
        path                                     | outputVersion
        'docker/devatherock/simple-slack/1.1.0'  | 'v1.1.0'
        'docker/devatherock/simple-slack/latest' | 'latest'
    }

    void 'test format download count'() {
        expect:
        dockerBadgeService.formatDownloadCount(downloadCount) == formattedCount

        where:
        downloadCount     | formattedCount
        450               | '450'
        1249              | '1.2k'
        1251              | '1.3k'
        1_100_000         | '1.1M'
        1_100_000_000     | '1.1G'
        1_100_000_000_000 | '1100G'
    }

    void 'test generate not found badge'() {
        when:
        String badge = dockerBadgeService.generateNotFoundBadge('layers')

        then:
        1 * badgeGenerator.generateBadge('layers', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }
}
