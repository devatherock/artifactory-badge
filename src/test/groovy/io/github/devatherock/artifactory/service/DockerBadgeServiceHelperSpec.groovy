package io.github.devatherock.artifactory.service

import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo

import spock.lang.Specification

/**
 * Test class for {@link DockerBadgeServiceHelper}
 */
class DockerBadgeServiceHelperSpec extends Specification {

    void 'test get version badge value'() {
        expect:
        DockerBadgeServiceHelper.getVersionBadgeValue(new ArtifactoryFolderInfo(path: path)) == outputVersion

        where:
        path                                     | outputVersion
        'docker/devatherock/simple-slack/1.1.0'  | 'v1.1.0'
        'docker/devatherock/simple-slack/latest' | 'latest'
    }

    void 'test format download count'() {
        expect:
        DockerBadgeServiceHelper.formatDownloadCount(downloadCount) == formattedCount

        where:
        downloadCount     | formattedCount
        450               | '450'
        1249              | '1.2k'
        1251              | '1.3k'
        1_100_000         | '1.1M'
        1_100_000_000     | '1.1G'
        1_100_000_000_000 | '1100G'
    }

    void 'test compare versions'() {
        expect:
        DockerBadgeServiceHelper.compareVersions(versionOne, versionTwo, 'major') == expectedResult

        where:
        versionOne    | versionTwo   | expectedResult
        '2'           | '1.1'        | 1
        '1'           | '1.1'        | -1
        '1.1-alpha'   | '1.1-beta'   | 0
        '1.1.1-alpha' | '1.1.1-beta' | 0
        '2.5-alpine'  | '2.5'        | 1
        '2.5'         | '2.5-alpine' | -1
        '1.1.1'       | '1.1.2'      | -1
        '1.1.2'       | '1.1.1'      | 1
        '1.1.2'       | '1.1.2'      | 0
    }
}
