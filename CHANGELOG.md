# Changelog

## [Unreleased]
### Added
- Initial version that generates docker pulls, image size and layers badges
- `/health` endpoint
- Custom badge generator
- [#16](https://github.com/devatherock/artifactory-badge/issues/16): Environment variables to configure log levels
- [#19](https://github.com/devatherock/artifactory-badge/issues/19): A `BlockingHttpClient` bean
- [#17](https://github.com/devatherock/artifactory-badge/issues/17): Documented environment variables that can be used to configure the application

### Changed
- Caught exception when JSON processing fails
- Used an explicit, injected HttpClient for artifactory so as to not URL encode slashes
- Used the IO thread pool instead of the default event loop
- Set the number of threads on the IO pool to `200`, to match spring boot's tomcat
- Upgraded JRE base image to `11.0.8` from `11.0.1` to fix SSL errors with TLS v1.3

### Removed
- [#13](https://github.com/devatherock/artifactory-badge/issues/13): Apache HTTP Client
