package io.github.devatherock.artifactory.service

import io.github.devatherock.artifactory.config.ArtifactoryProperties
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo
import io.github.devatherock.artifactory.util.BadgeGenerator
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link DockerBadgeService}
 */
class DockerBadgeServiceSpec extends Specification {
    @Subject
    DockerBadgeService dockerBadgeService

    BlockingHttpClient httpClient = HttpClient.create(new URL('http://localhost:8081')).toBlocking()
    BadgeGenerator badgeGenerator = Mock()
    ArtifactoryProperties config = new ArtifactoryProperties(url: 'http://localhost:8081')

    void setup() {
        config.init()
        dockerBadgeService = new DockerBadgeService(httpClient, badgeGenerator, config)
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
}
