package io.github.devatherock.logback

import java.nio.file.Paths

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test class for {@link LogbackConfigInitializer}
 */
class LogbackConfigInitializerSpec extends Specification {
    static final String PROP_LOGGING_CONFIG = 'logger.config'

    void cleanup() {
        System.clearProperty(PROP_LOGGING_CONFIG)
    }

    void 'test initialize config - no args'() {
        when:
        LogbackConfigInitializer.initializeConfig()

        then:
        noExceptionThrown()
    }

    @Unroll
    void 'test read config - #configFile'() {
        when:
        InputStream stream = LogbackConfigInitializer.readConfig(configFile)

        then:
        stream
        System.properties[PROP_LOGGING_CONFIG] == loggingConfig

        cleanup:
        stream?.close()

        where:
        configFile << [
            'logback.xml',
            Paths.get(System.properties['user.dir'], 'src/main/resources/logback.xml').toString(),
            'https://raw.githubusercontent.com/devatherock/artifactory-badge/master/src/main/resources/logback-json.xml'
        ]
        loggingConfig << [
            'logback.xml',
            Paths.get(System.properties['user.dir'], 'src/main/resources/logback.xml').toString(),
            null,
        ]
    }

    @Unroll
    void 'test read config - non-existent/invalid path - #configFile'() {
        when:
        def stream = LogbackConfigInitializer.readConfig(configFile)

        then:
        !stream
        !System.properties[PROP_LOGGING_CONFIG]

        where:
        configFile << [
            'classpath:logback.xml',
            'dummy.xml',
            Paths.get(System.properties['user.dir'], 'src/main/resources/logbackx.xml').toString(),
            'https://raw.githubusercontent.com/devatherock/artifactory-badge/master/src/main/resour/logback-json.xml'
        ]
    }

    @Unroll
    void 'test initialize config - #configFile'() {
        when:
        LogbackConfigInitializer.initializeConfig(configFile)

        then:
        System.properties[PROP_LOGGING_CONFIG] == loggingConfig

        where:
        configFile << [
            'logback.xml',
            Paths.get(System.properties['user.dir'], 'src/main/resources/logback.xml').toString(),
            'https://raw.githubusercontent.com/devatherock/artifactory-badge/master/src/main/resources/logback-json.xml',
            'classpath:logback.xml',
            'logback-invalid.xml',
            'application-test.yml',
        ]
        loggingConfig << [
                'logback.xml',
                Paths.get(System.properties['user.dir'], 'src/main/resources/logback.xml').toString(),
                null,
                null,
                'logback-invalid.xml',
                null,
        ]
    }
}
