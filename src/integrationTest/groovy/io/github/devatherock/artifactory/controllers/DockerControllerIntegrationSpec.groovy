package io.github.devatherock.artifactory.controllers

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Integration test for {@link DockerController}
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class DockerControllerIntegrationSpec extends DockerControllerSpec {
}
