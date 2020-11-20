package io.github.devatherock.artifactory.config;

import javax.annotation.PostConstruct;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.AccessLevel;
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

    /**
     * The {@code shields.io} URL
     */
    private String url = "https://img.shields.io";

    @Setter(AccessLevel.NONE)
    private String badgeUrl;

    @PostConstruct
    public void init() {
        badgeUrl = url + "/static/v1";
    }
}
