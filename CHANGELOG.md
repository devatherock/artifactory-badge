# Changelog

## [Unreleased]
### Added
- Used `circleci-templates` orb to simplify CI pipeline

### Changed
- Specified all generated json config files to the `native-image` command
- chore(deps): update plugin io.micronaut.application to v3.7.10
- chore(deps): update plugin org.sonarqube to v4.2.1.3168
- fix(deps): update dependency ch.qos.logback:logback-classic to v1.4.8
- chore(deps): update devatherock/simple-slack docker tag to v1
- fix(deps): update dependency net.logstash.logback:logstash-logback-encoder to v7.4
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.9.4
- chore(deps): update plugin org.owasp.dependencycheck to v8.3.1
- chore(deps): update dependency gradle to v7.6.2
- fix(deps): update dependency org.codehaus.janino:janino to v3.1.10
- chore(deps): update plugin com.diffplug.spotless to v6.20.0
- chore(deps): update plugin org.sonarqube to v4.3.0.3225
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.10.0
- fix(deps): update dependency ch.qos.logback:logback-classic to v1.4.9
- fix(deps): update dependency ch.qos.logback:logback-classic to v1.4.11
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.10.1
- chore(deps): update plugin org.sonarqube to v4.3.1.3277
- chore(deps): update plugin com.diffplug.spotless to v6.21.0
- chore(deps): update plugin org.owasp.dependencycheck to v8.4.0
- fix(deps): update dependency com.github.tomakehurst:wiremock to v3
- fix(deps): update dependency org.projectlombok:lombok to v1.18.30
- chore(deps): update plugin com.diffplug.spotless to v6.22.0
- chore(deps): update dependency gradle to v7.6.3
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.10.2
- fix(deps): update dependency org.jsoup:jsoup to v1.16.2
- chore(deps): update plugin org.owasp.dependencycheck to v8.4.2
- chore(deps): update plugin org.sonarqube to v4.4.1.3373
- chore: Made the gradle-includes location configurable
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.10.3
- chore(deps): update plugin org.owasp.dependencycheck to v8.4.3
- chore(deps): update plugin org.owasp.dependencycheck to v9
- Configure Mend Bolt for GitHub
- chore(deps): update plugin org.owasp.dependencycheck to v9.0.2
- fix(deps): update dependency ch.qos.logback:logback-classic to v1.4.14
- fix(deps): update dependency org.codehaus.janino:janino to v3.1.11
- chore(deps): update plugin com.diffplug.spotless to v6.23.2
- fix(deps): update dependency org.jsoup:jsoup to v1.17.1

## [2.0.0] - 2023-06-03
### Added
- Tests for framework provided endpoints like `/health`
- `yaml-validator` to CI pipeline

### Changed
- fix(deps): update dependency org.objenesis:objenesis to v3.3
- Improved the API spec using additional annotations
- feat: Updated changelog updater user
- fix(deps): update dependency org.codehaus.janino:janino to v3.1.9
- fix(deps): update dependency com.fasterxml.jackson.core:jackson-databind to v2.14.1
- chore(deps): update plugin com.github.kt3k.coveralls to v2.12.2
- chore(deps): update docker orb to v2.2.0
- chore(deps): update plugin org.owasp.dependencycheck to v8.2.1
- fix(deps): update dependency net.logstash.logback:logstash-logback-encoder to v7.3
- fix(deps): update dependency ch.qos.logback:logback-classic to v1.4.7
- fix(deps): update dependency org.jsoup:jsoup to v1.16.1
- Used custom `publish` step instead of docker orb, to fix the failing multi-stage build
- Upgraded to gradle 7 and Java 17
- The log pattern
- Fixed codenarc violations
- fix(deps): update dependency io.micronaut:micronaut-bom to v3.9.2
- Updated dockerhub readme in CI pipeline
- fix(deps): update dependency org.projectlombok:lombok to v1.18.28
- chore(deps): update plugin com.diffplug.spotless to v6.19.0
- chore(deps): update plugin org.sonarqube to v4.2.0.3129
- [#249](https://github.com/devatherock/artifactory-badge/issues/249): Built separate docker images for arm64 and x86
- [#233](https://github.com/devatherock/artifactory-badge/issues/233): Fixed the error when upgrading graalvm from `22.1.0` to `22.3.2`

### Removed
- `Jansi` as it was preventing the app from running in local

## [1.2.0] - 2022-05-15
### Added
- [#167](https://github.com/devatherock/artifactory-badge/issues/167): Tests for `ArtifactoryController`

### Changed
- [#163](https://github.com/devatherock/artifactory-badge/issues/163): Used distroless base docker image
- [#169](https://github.com/devatherock/artifactory-badge/issues/169): Fixed badge generation timeout due to missing reflection config for `io.netty.handler.ssl.SslHandler`

## [1.1.0] - 2022-05-12
### Added
- Support for reading logback config from local or remote file

### Changed
- Used integration test config from `gradle-includes`

## [1.0.0] - 2022-05-08
### Added
- [#51](https://github.com/devatherock/artifactory-badge/issues/51): Integration tests
- [#14](https://github.com/devatherock/artifactory-badge/issues/14): Used native binary in docker image
- [#159](https://github.com/devatherock/artifactory-badge/issues/159): Environment variable to accept logback config path. Needed as it wasn't clear how to set the JVM arg `logback.configurationFile` with the graalvm binary

### Changed
- chore(deps): update plugin io.micronaut.application to v2.0.8
- chore: Set renovate's email for changelog-updater plugin
- chore(deps): update dependency ch.qos.logback:logback-classic to v1.2.11
- chore(deps): update dependency gradle to v6.9.2
- chore(deps): update dependency net.logstash.logback:logstash-logback-encoder to v7.1.1
- chore(deps): update dependency org.spockframework:spock-core to v2.1-groovy-3.0
- chore(deps): update dependency cimg/openjdk to v17
- chore: Used custom ssh key to push to github
- chore(deps): update dependency io.micronaut:micronaut-bom to v3.4.3
- chore(deps): update dependency org.codehaus.janino:janino to v3.1.7
- chore(deps): update dependency org.projectlombok:lombok to v1.18.24
- chore(deps): update docker orb to v2.1.1
- chore(deps): update plugin com.diffplug.spotless to v6.5.2
- chore(deps): update plugin org.owasp.dependencycheck to v7.1.0.1

## [0.5.0] - 2021-10-29
### Added
- [#107](https://github.com/devatherock/artifactory-badge/issues/107): `janino` to have conditions in logging config
- `dependencycheck` gradle plugin to detect vulnerable dependencies

### Changed
- chore: Added changelog-updater for creating missed changelog entries
- chore(deps): update dependency org.projectlombok:lombok to v1.18.22
- chore(deps): update plugin com.diffplug.spotless to v5.17.0
- chore(deps): update plugin io.micronaut.application to v2.0.7

## [0.4.0] - 2021-07-24
### Added
- Spotless gradle plugin to format code

### Changed
- [#40](https://github.com/devatherock/artifactory-badge/issues/40): To not treat `_uploads` subfolder as a docker tag
- [#98](https://github.com/devatherock/artifactory-badge/issues/98): Date format used to parse dates in API responses

### Removed
- Custom environment variables with `LOGGING_LEVEL` prefix and updated documentation to use environment variables 
with `LOGGER_LEVELS` prefix supported out of the box by micronaut

## [0.3.0] - 2021-04-03
### Added
- Support for JSON logs

### Changed
- chore: Upgraded micronaut from 2.1.0 to 2.4.2
- fix: Set log level of `NettyHttpServer` to `TRACE` to show logs in micronaut 2.3.x

## [0.2.1] - 2020-11-20
### Changed
- Corrected docker image for release job in build pipeline

## [0.2.0] - 2020-11-20
### Added
- Initial version that generates docker pulls, image size and layers badges
- `/health` endpoint
- Custom badge generator
- [#16](https://github.com/devatherock/artifactory-badge/issues/16): Environment variables to configure log levels
- [#19](https://github.com/devatherock/artifactory-badge/issues/19): A `BlockingHttpClient` bean
- [#17](https://github.com/devatherock/artifactory-badge/issues/17): Documented environment variables that can be used to configure the application
- [#20](https://github.com/devatherock/artifactory-badge/issues/20): `/version` endpoint to generate latest version badge
- `/metrics` endpoint
- Enabled access logs
- [#24](https://github.com/devatherock/artifactory-badge/issues/24): Unit tests for 100% code coverage
- [#31](https://github.com/devatherock/artifactory-badge/issues/31): `sort` parameter to `/version` endpoint with default value as `date`. With value `semver`, it'll pull the latest out of only semantic version tags

### Changed
- Caught exception when JSON processing fails
- Used an explicit, injected HttpClient for artifactory so as to not URL encode slashes
- Used the IO thread pool instead of the default event loop
- Set the number of threads on the IO pool to `200`, to match spring boot's tomcat
- Upgraded JRE base image to `11.0.8` from `11.0.1` to fix SSL errors with TLS v1.3
- [#10](https://github.com/devatherock/artifactory-badge/issues/10): Hid decimal point when image size is a whole number
- [#11](https://github.com/devatherock/artifactory-badge/issues/11): Shortened pulls count
- [#25](https://github.com/devatherock/artifactory-badge/issues/25): Used a different `shields.io` URL, so that badge values with `-` are supported
- [#15](https://github.com/devatherock/artifactory-badge/issues/15): Handled HTTP 404s from artifactory APIs
- [#35](https://github.com/devatherock/artifactory-badge/issues/35): Handled HTTP errors from `shields.io`

### Removed
- [#13](https://github.com/devatherock/artifactory-badge/issues/13): Apache HTTP Client