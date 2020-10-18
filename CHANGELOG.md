# Changelog

## [Unreleased]
### Added
- Initial version that generates docker pulls, image size and layers badges
- `/health` endpoint
- Custom badge generator

### Changed
- Caught exception when JSON processing fails
- Used an explicit HttpClient so as to not URL encode slashes
- Enabled SSL. Used injected HttpClient
- Used Apache HTTP client to generate the badge