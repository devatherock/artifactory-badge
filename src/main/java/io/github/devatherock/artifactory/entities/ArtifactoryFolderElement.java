package io.github.devatherock.artifactory.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing an element within a folder
 */
@Getter
@Setter
@Builder
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactoryFolderElement {
    private String uri;
    private boolean folder;
}
