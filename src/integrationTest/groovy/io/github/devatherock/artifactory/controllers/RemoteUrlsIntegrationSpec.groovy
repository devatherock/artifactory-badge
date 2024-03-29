package io.github.devatherock.artifactory.controllers

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Integration test that calls remote endpoints
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class RemoteUrlsIntegrationSpec extends RemoteUrlsSpec {
}
