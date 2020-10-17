package io.github.devatherock.artifactory.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
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
