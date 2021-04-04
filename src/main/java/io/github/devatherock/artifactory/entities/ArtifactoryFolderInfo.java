package io.github.devatherock.artifactory.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

/**
 * Class representing artifactory folder info
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactoryFolderInfo {
    @Singular("child")
    private List<ArtifactoryFolderElement> children;

    private String lastModified;

    private String path;
}
