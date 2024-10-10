package io.github.devatherock.artifactory.controllers

import java.nio.file.Files
import java.nio.file.Paths

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Unit test for additional endpoints
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class AdditionalControllerIntegrationSpec extends AdditionalEndpointsSpec {

    void 'test log format'() {
        expect:
        Files.readString(Paths.get('logs-intg.txt')).contains('"message":"Startup completed')
    }
}
