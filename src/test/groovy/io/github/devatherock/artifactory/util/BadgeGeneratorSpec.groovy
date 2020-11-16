package io.github.devatherock.artifactory.util

import io.github.devatherock.artifactory.config.ShieldsIOProperties
import io.github.devatherock.artifactory.service.ShieldsIOClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * Test class for {@link BadgeGenerator}
 */
class BadgeGeneratorSpec extends Specification {
    @Subject
    BadgeGenerator badgeGenerator

    ShieldsIOClient shieldsIOClient = Mock()
    ShieldsIOProperties config = new ShieldsIOProperties(enabled: false)

    void setup() {
        badgeGenerator = new BadgeGenerator(shieldsIOClient, config)
    }

    void 'test generate badge - custom generator'() {
        given:
        String expectedResponse = [
                '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="144" height="20" role="img" aria-label="docker pulls: 47">',
                '<title>docker pulls: 47</title>',
                '<linearGradient id="s" x2="0" y2="100%">',
                '<stop offset="0" stop-color="#bbb" stop-opacity=".1"/>',
                '<stop offset="1" stop-opacity=".1"/>',
                '</linearGradient>',
                '<clipPath id="r">',
                '<rect width="144" height="20" rx="3" fill="#fff"/>',
                '</clipPath>',
                '<g clip-path="url(#r)">',
                '<rect width="112" height="20" fill="#555"/>',
                '<rect x="112" width="32" height="20" fill="#007ec6"/>',
                '<rect width="144" height="20" fill="url(#s)"/>',
                '</g>',
                '<g font-family="monospace">',
                '<text aria-hidden="true" x="0" y="15" fill="#fff" xml:space="preserve"> docker pulls </text>',
                '<text aria-hidden="true" x="112" y="15" fill="#fff" xml:space="preserve"> 47 </text>',
                '</g>',
                '</svg>'
        ].join('')

        when:
        String badge = badgeGenerator.generateBadge('docker pulls', '47')

        then:
        badge == expectedResponse
    }
}
