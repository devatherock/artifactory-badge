package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("artifactory.badge.shields-io")
public class ShieldsIOProperties {
    private boolean enabled = true;
    private boolean customHttp = true;

    private String url = "https://img.shields.io/badge";
}
