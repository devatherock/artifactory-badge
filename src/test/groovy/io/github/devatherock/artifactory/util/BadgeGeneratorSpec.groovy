package io.github.devatherock.artifactory.util

import io.github.devatherock.artifactory.config.ShieldsIOProperties
import io.github.devatherock.test.TestUtil

import io.micronaut.http.client.BlockingHttpClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link BadgeGenerator}
 */
class BadgeGeneratorSpec extends Specification {
    @Subject
    BadgeGenerator badgeGenerator

    BlockingHttpClient shieldsIOClient = Mock()
    ShieldsIOProperties config = new ShieldsIOProperties(enabled: false)

    void setup() {
        badgeGenerator = new BadgeGenerator(shieldsIOClient, config)
    }

    void 'test generate badge - custom generator'() {
        when:
        String badge = badgeGenerator.generateBadge('docker pulls', '47')

        then:
        badge == TestUtil.getCustomBadgeResponse()
    }
}
