package io.github.devatherock.artifactory.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.devatherock.artifactory.config.ArtifactoryProperties
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo
import io.github.devatherock.artifactory.util.BadgeGenerator
import io.github.devatherock.test.TestUtil
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

    void 'test get image size badge - manifest without layers'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson('{}')))

        when:
        String badge = dockerBadgeService.getImageSizeBadge(packageName, 'latest', 'image size')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('image size', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get image layers badge - manifest not found'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getImageLayersBadge(packageName, 'latest', 'layers')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('layers', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get image layers badge - manifest without layers'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/${packageName}/latest/manifest.json")
                .willReturn(WireMock.okJson('{}')))

        when:
        String badge = dockerBadgeService.getImageLayersBadge(packageName, 'latest', 'layers')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/${packageName}/latest/manifest.json"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('layers', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get pulls count badge - image not found'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getPullsCountBadge(packageName, 'docker pulls')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('docker pulls', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get pulls count badge - only image root folder exists'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson('{}')))

        when:
        String badge = dockerBadgeService.getPullsCountBadge(packageName, 'docker pulls')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('docker pulls', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get pulls count badge - folder is not a docker image and contains files'() {
        given:
        String packageName = 'io/github/devatherock/simple-yaml'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(TestUtil.getFolderWithFileResponse())))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getPullsCountBadge(packageName, 'docker pulls')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0/manifest.json?stats"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/maven-metadata.xml/manifest.json?stats")))
        1 * badgeGenerator.generateBadge('docker pulls', '0') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - image not found'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getLatestVersionBadge(packageName, 'version')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('version', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - only image root folder exists'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson('{}')))

        when:
        String badge = dockerBadgeService.getLatestVersionBadge(packageName, 'version')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        1 * badgeGenerator.generateBadge('version', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - folder is not a docker image and contains files'() {
        given:
        String packageName = 'io/github/devatherock/simple-yaml'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(TestUtil.getFolderWithFileResponse())))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0")
                .willReturn(WireMock.okJson(TestUtil.getFoldersResponse("${packageName}/1.1.0", '2020-10-01T00:00:00.000Z'))))

        when:
        String badge = dockerBadgeService.getLatestVersionBadge(packageName, 'version')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/1.1.0"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/maven-metadata.xml")))
        1 * badgeGenerator.generateBadge('version', 'v1.1.0') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - folder is not a docker image and contains only files'() {
        given:
        String packageName = 'io/github/devatherock/simple-yaml'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(TestUtil.getFolderWithOnlyFileResponse())))

        when:
        String badge = dockerBadgeService.getLatestVersionBadge(packageName, 'version')

        then:
        WireMock.verify(1,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}"))
                        .withHeader(DockerBadgeService.HDR_API_KEY, equalTo('dummyKey')))
        WireMock.verify(0,
                WireMock.getRequestedFor(urlEqualTo("/artifactory/api/storage/${packageName}/maven-metadata.xml")))
        1 * badgeGenerator.generateBadge('version', 'Not Found') >> 'dummyBadge'
        badge == 'dummyBadge'
    }

    void 'test get latest version badge - exception when fetching version modified time'() {
        given:
        String packageName = 'docker/devatherock/simple-slack'

        and:
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}")
                .willReturn(WireMock.okJson(TestUtil.getFoldersResponse(packageName, '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.0")
                .willReturn(WireMock.okJson(TestUtil.getFoldersResponse("${packageName}/1.1.0", '2020-10-01T00:00:00.000Z'))))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/1.1.2")
                .willReturn(WireMock.notFound()))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/latest")
                .willReturn(WireMock.notFound()))
        WireMock.givenThat(WireMock.get("/artifactory/api/storage/${packageName}/abcdefgh")
                .willReturn(WireMock.notFound()))

        when:
        String badge = dockerBadgeService.getLatestVersionBadge(packageName, 'version')

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
        1 * badgeGenerator.generateBadge('version', 'v1.1.0') >> 'dummyBadge'
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
