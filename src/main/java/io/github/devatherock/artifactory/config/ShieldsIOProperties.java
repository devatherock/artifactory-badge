package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("artifactory.badge.shields-io")
public class ShieldsIOProperties {
    /**
     * Indicates if <a href="https://shields.io">shields.io</a> should be used to
     * generate the badge
     */
    private boolean enabled = true;
}
