package io.github.devatherock.artifactory.entities;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class DockerLayer {
    private long size;
}
