package io.github.devatherock.artifactory.service;

import io.github.devatherock.artifactory.entities.ArtifactoryFileStats;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client(value = "${artifactory.url}/artifactory")
public interface ArtifactoryClient {

    @Get(value = "/{filePath}")
    String getFileContent(@PathVariable String filePath, @Header(name = "X-JFrog-Art-Api") String apiKey);

    @Get(value = "/api/storage/{folderName}")
    ArtifactoryFolderInfo getFolderInfo(@PathVariable String folderName,
                                        @Header(name = "X-JFrog-Art-Api") String apiKey);

    @Get(value = "/api/storage/{filePath}?stats")
    ArtifactoryFileStats getFileStats(@PathVariable String filePath,
                                      @Header(name = "X-JFrog-Art-Api") String apiKey);
}
