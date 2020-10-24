# Changelog

## [Unreleased]
### Added
- Initial version that generates docker pulls, image size and layers badges
- `/health` endpoint
- Custom badge generator
- [Issue 16](https://github.com/devatherock/artifactory-badge/issues/16): Environment variables to configure log levels

### Changed
- Caught exception when JSON processing fails
- Used an explicit HttpClient so as to not URL encode slashes
- Enabled SSL. Used injected HttpClient
- Used Apache HTTP client to generate the badge
- Used the IO thread pool instead of the default event loop
- Set the number of threads on the IO pool to `200`, to match spring boot's tomcat