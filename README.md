[![CircleCI](https://circleci.com/gh/devatherock/artifactory-badge.svg?style=svg)](https://circleci.com/gh/devatherock/artifactory-badge)
[![Version](https://img.shields.io/docker/v/devatherock/artifactory-badge?sort=semver)](https://hub.docker.com/r/devatherock/artifactory-badge/)
[![Coverage Status](https://coveralls.io/repos/github/devatherock/artifactory-badge/badge.svg?branch=master)](https://coveralls.io/github/devatherock/artifactory-badge?branch=master)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=artifactory-badge&metric=alert_status)](https://sonarcloud.io/component_measures?id=artifactory-badge&metric=alert_status&view=list)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/artifactory-badge.svg)](https://hub.docker.com/r/devatherock/artifactory-badge/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/artifactory-badge.svg?sort=date)](https://hub.docker.com/r/devatherock/artifactory-badge/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# artifactory-badge
Badge generator for docker registries hosted in jfrog artifactory

## Usage
### Sample command
```
docker run --rm \
  -p 8080:8080 \
  -e ARTIFACTORY_URL=https://some/url \
  -e ARTIFACTORY_API_KEY=xyz \
  devatherock/artifactory-badge:1.1.0
```

### Configurable properties
#### application.yml

```yaml
logger:
  levels:
    io.micronaut.http.server.netty.NettyRequestLifecycle: DEBUG # Optional. To enable HTTP server access logs
```

#### Environment variables

| Name                                 |   Required   | Default                      | Description                                                                                                                                    |
|--------------------------------------|--------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| ARTIFACTORY_URL                      |   true       | (None)                       | The JFrog artifactory URL that hosts the docker registry                                                                                       |
| ARTIFACTORY_API_KEY                  |   true       | (None)                       | API key for interacting with artifactory's REST API                                                                                            |
| ARTIFACTORY_EXCLUDED_FOLDERS         |   false      | /_uploads                    | Subfolders to be not treated as docker tags                                                                                                    |
| ARTIFACTORY_DATE_FORMAT              |   false      | yyyy-MM-dd'T'HH:mm:ss.SSSXXX | Date format to parse dates in artifactory API responses                                                                                        |
| ARTIFACTORY_BADGE_SHIELDS_IO_ENABLED |   false      | true                         | Indicates if <a href="https://shields.io">shields.io</a> should be used to generate the badge                                                  |
| ARTIFACTORY_BADGE_PARALLELISM        |   false      | 5                            | Amount of parallelism to use when fetching details about versions of an image                                                                  |
| LOGGER_LEVELS_ROOT                   |   false      | INFO                         | [SLF4J](http://www.slf4j.org/api/org/apache/commons/logging/Log.html) log level, for all(framework and custom) code                            |
| LOGGER_LEVELS_IO_GITHUB_DEVATHEROCK  |   false      | INFO                         | [SLF4J](http://www.slf4j.org/api/org/apache/commons/logging/Log.html) log level, for custom code                                               |
| MICRONAUT_ENVIRONMENTS               |   false      | (None)                       | Setting the value to `local` will mock the calls to the artifactory. Only for testing purposes                                                 |
| MICRONAUT_SERVER_PORT                |   false      | 8080                         | Port in which the app listens on                                                                                                               |
| MICRONAUT_CONFIG_FILES               |   true       | (None)                       | Path to YAML config files. The YAML files can be used to specify complex, object and array properties                                          |
| LOGBACK_CONFIGURATION_FILE           |   false      | (None)                       | Class, file or remote path to logback configuration file. Will be ignored when using a remote path with any `logger.*` or `LOGGER_` config set |

### API spec
When the app is running, detailed API documentation can be accessed at `{host}/swagger-ui` or `{host}/swagger/artifactory-badge-{version}.yml`. The available endpoints are listed below for reference:

- `/docker/pulls?package=path/to/package` - Generates download count badge
- `/docker/image-size?package=path/to/package` - Generates docker image size badge
- `/docker/layers?package=path/to/package` - Generates docker image layers count badge
- `/version?package=path/to/package` - Generates latest version badge

### Sample badge generated by the custom generator
```xml
<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="144" height="20" role="img" aria-label="docker pulls: 47">
    <title>docker pulls: 47</title>
    <linearGradient id="s" x2="0" y2="100%">
        <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
        <stop offset="1" stop-opacity=".1"/>
    </linearGradient>
    <clipPath id="r">
        <rect width="144" height="20" rx="3" fill="#fff"/>
    </clipPath>
    <g clip-path="url(#r)">
        <rect width="112" height="20" fill="#555"/>
        <rect x="112" width="32" height="20" fill="#007ec6"/>
        <rect width="144" height="20" fill="url(#s)"/>
    </g>
    <g font-family="monospace">
        <text aria-hidden="true" x="0" y="15" fill="#fff" xml:space="preserve"> docker pulls </text>
        <text aria-hidden="true" x="112" y="15" fill="#fff" xml:space="preserve"> 47 </text>
    </g>
</svg>
```

## Troubleshooting
### Enabling debug logs
- Set the environment variable `LOGGER_LEVELS_ROOT` to `DEBUG` to enable all debug logs - custom and framework
- Set the environment variable `LOGGER_LEVELS_IO_GITHUB_DEVATHEROCK` to `DEBUG` to enable debug logs only in custom code
- For fine-grained logging control, supply a custom [logback.xml](http://logback.qos.ch/manual/configuration.html) file 
and set the environment variable `LOGBACK_CONFIGURATION_FILE` to `/path/to/custom/logback.xml`

### JSON logs
Refer [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder) documentation to customize the field names and formats in the log. To output logs as JSON, set the environment variable `LOGBACK_CONFIGURATION_FILE` to `logback-json.xml`
