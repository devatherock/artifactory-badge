package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Context
@ConfigurationProperties("artifactory")
public class ArtifactoryProperties {
	@NotBlank(message = "artifactory.url not specified")
	private String url;

	@NotBlank(message = "artifactory.api-key not specified")
	private String apiKey;
}
