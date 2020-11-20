package io.github.devatherock.artifactory.config

import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link ShieldsIOProperties}
 */
class ShieldsIOPropertiesSpec extends Specification {
    @Subject
    ShieldsIOProperties config = new ShieldsIOProperties()

    void 'test init'() {
        when:
        config.init()

        then:
        config.badgeUrl == 'https://img.shields.io/static/v1'
    }
}
