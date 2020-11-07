package io.github.devatherock.test;

import io.github.devatherock.artifactory.entities.ArtifactoryFileStats;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderElement;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;
import io.github.devatherock.artifactory.entities.DockerLayer;
import io.github.devatherock.artifactory.entities.DockerManifest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Hidden
@Controller("/artifactory")
public class ArtifactoryController {

    @Get(value = "/{fileName:.*}", produces = MediaType.APPLICATION_JSON)
    public DockerManifest getFileContent(@PathVariable String fileName,
                                         @Header(value = "X-JFrog-Art-Api") String apiKey) {
        LOGGER.info("API key in getFileContent {}: {}", fileName, apiKey);
        return DockerManifest.builder()
                .layer(new DockerLayer(28311552)) // 27 MB
                .layer(new DockerLayer(56623104)) // 54 MB
                .layer(new DockerLayer(10485760)) // 10 MB
                .layer(new DockerLayer(9437184)) // 9 MB
                .layer(new DockerLayer(524288)) // 0.5 MB
                .layer(new DockerLayer(536871)) // 0.51200008 MB
                .layer(new DockerLayer(786432)) // 0.75 MB
                .build();
    }

    @Get(value = "/api/storage/{folderName:.*}", produces = MediaType.APPLICATION_JSON)
    public Object getFolderInfo(@PathVariable String folderName,
                                @Header(value = "X-JFrog-Art-Api") String apiKey) {
        LOGGER.info("API key in getFolderInfo {}: {}", folderName, apiKey);

        if (folderName.endsWith("manifest.json")) {
            return new ArtifactoryFileStats(450);
        } else {
            return ArtifactoryFolderInfo.builder()
                    .child(ArtifactoryFolderElement.builder().uri("/1.1.0").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/1.1.2").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/latest").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/dummy").folder(false).build())
                    .build();
        }
    }
}
