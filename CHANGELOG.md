# Changelog

## [Unreleased]
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
