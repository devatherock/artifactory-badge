package io.github.devatherock.artifactory.config

import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link ArtifactoryProperties}
 */
class ArtifactoryPropertiesSpec extends Specification {
    @Subject
    ArtifactoryProperties config = new ArtifactoryProperties()

    void setup() {
        config.url = 'http://localhost:8081'
    }

    void 'test init'() {
        when:
        config.init()

        then:
        config.urlPrefix == 'http://localhost:8081/artifactory/'
        config.storageUrlPrefix == 'http://localhost:8081/artifactory/api/storage/'
    }
}
