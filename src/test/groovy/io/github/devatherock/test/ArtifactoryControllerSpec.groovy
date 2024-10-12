package io.github.devatherock.test

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test class for {@link ArtifactoryController}
 */
@MicronautTest
class ArtifactoryControllerSpec extends Specification {

    @Inject
    @Client('${test.server.url}')
    HttpClient httpClient

    void 'test get file content'() {
        when:
        String manifest = httpClient.toBlocking().retrieve(
                HttpRequest.GET('/artifactory/manifest.json')
                        .header('X-JFrog-Art-Api', 'dummy'))

        then:
        manifest.contains('layers')
    }

    @Unroll
    void 'test get folder info - #path'() {
        when:
        String folderInfo = httpClient.toBlocking().retrieve(
                HttpRequest.GET("/artifactory/${path}")
                        .header('X-JFrog-Art-Api', 'dummy'))

        then:
        folderInfo.contains(response)

        where:
        path                        | response
        'api/storage/manifest.json' | 'downloadCount'
        'api/storage/latest'        | 'lastModified'
        'api/storage/notfound'      | 'uri'
    }
}
