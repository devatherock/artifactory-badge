package io.github.devatherock.artifactory.config;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context
@ConfigurationProperties("artifactory")
public class ArtifactoryProperties {
    /**
     * The JFrog artifactory URL that hosts the docker registry
     */
    @NotBlank(message = "artifactory.url not specified")
    private String url;

    /**
     * API key for interacting with artifactory's REST API
     */
    @NotBlank(message = "artifactory.api-key not specified")
    private String apiKey;

    @Setter(AccessLevel.NONE)
    private String urlPrefix;

    @Setter(AccessLevel.NONE)
    private String storageUrlPrefix;

    @PostConstruct
    public void init() {
        urlPrefix = url + "/artifactory/";
        storageUrlPrefix = urlPrefix + "api/storage/";
    }
}
