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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Hidden
@Controller("/artifactory")
public class ArtifactoryController {
    private static final Map<Integer, Long> PULLS_RANDOMIZER = new HashMap<>();
    private static final Map<String, String> MODIFIED_TIME = new HashMap<>();

    static {
        PULLS_RANDOMIZER.put(0, 333l);
        PULLS_RANDOMIZER.put(1, 450l);
        PULLS_RANDOMIZER.put(2, 450_000l);
        PULLS_RANDOMIZER.put(3, 450_000_000l);
        PULLS_RANDOMIZER.put(4, 450_000_000_000l);
        MODIFIED_TIME.put("latest", "2020-10-01T00:00:00.000Z");
        MODIFIED_TIME.put("1.1.0", "2020-10-08T00:00:00.000Z");
        MODIFIED_TIME.put("1.1.2-rc.1", "2020-10-15T00:00:00.000Z");
    }

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
            int randomNumber = (int) (Math.random() * PULLS_RANDOMIZER.size());
            return new ArtifactoryFileStats(PULLS_RANDOMIZER.get(randomNumber));
        } else if (MODIFIED_TIME.keySet().stream().anyMatch(key -> folderName.endsWith(key))) {
            return ArtifactoryFolderInfo.builder()
                    .lastModified(MODIFIED_TIME.get(folderName.substring(folderName.lastIndexOf('/') + 1)))
                    .path(folderName)
                    .build();
        } else {
            return ArtifactoryFolderInfo.builder()
                    .child(ArtifactoryFolderElement.builder().uri("/1.1.0").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/1.1.2-rc.1").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/latest").folder(true).build())
                    .child(ArtifactoryFolderElement.builder().uri("/dummy").folder(false).build())
                    .build();
        }
    }
}
