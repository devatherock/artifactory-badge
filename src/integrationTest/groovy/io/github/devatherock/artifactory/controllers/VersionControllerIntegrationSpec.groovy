package io.github.devatherock.artifactory.controllers

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Integration test for {@link VersionController}
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class VersionControllerIntegrationSpec extends VersionControllerSpec {
}
