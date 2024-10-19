package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("artifactory.badge")
public class AppProperties {
    /**
     * Amount of parallelism to use when fetching details about versions of an image
     */
    private int parallelism = 5;
}
