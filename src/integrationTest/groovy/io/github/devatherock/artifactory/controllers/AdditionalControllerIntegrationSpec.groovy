package io.github.devatherock.artifactory.controllers

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Unit test for additional endpoints
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class AdditionalControllerIntegrationSpec extends AdditionalEndpointsSpec {
}
