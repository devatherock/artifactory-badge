# Changelog

## [Unreleased]
### Changed
- chore(deps): update plugin com.diffplug.spotless to v5.17.1
- chore(deps): update plugin io.micronaut.application to v2.0.8
- chore(deps): update cimg/openjdk docker tag to v17

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