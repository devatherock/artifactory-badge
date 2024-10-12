package io.github.devatherock.artifactory.controllers

import java.nio.file.Files
import java.nio.file.Paths

import io.micronaut.test.extensions.spock.annotation.MicronautTest

/**
 * Integration test that calls remote endpoints
 */
@MicronautTest(propertySources = 'classpath:application-integration.yml', startApplication = false)
class RemoteUrlsIntegrationSpec extends RemoteUrlsSpec {

    void 'test log output'() {
        when:
        String logs = Files.readString(Paths.get('logs-intg-remote.txt'))

        then:
        logs.contains('INFO  io.micronaut.runtime.Micronaut - Startup completed')
        logs.contains('Request GET /docker/image-size')
    }
}
