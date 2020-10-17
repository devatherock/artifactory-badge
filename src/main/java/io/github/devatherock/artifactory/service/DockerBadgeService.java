package io.github.devatherock.artifactory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.devatherock.artifactory.config.ArtifactoryProperties;
import io.github.devatherock.artifactory.entities.ArtifactoryFileStats;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderElement;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;
import io.github.devatherock.artifactory.entities.DockerLayer;
import io.github.devatherock.artifactory.entities.DockerManifest;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class DockerBadgeService {
    private static final double BYTES_IN_MB = 1024 * 1024;
    private static final String FILE_NAME_MANIFEST = "/manifest.json";

    private final ArtifactoryClient artifactoryClient;
    private final ShieldsIOClient shieldsIOClient;
    private final ObjectMapper objectMapper;
    private final ArtifactoryProperties artifactoryConfig;

    @Cacheable(cacheNames = "size-cache")
    public String getImageSizeBadge(String packageName, String tag, String badgeLabel) {
        DockerManifest manifest = readManifest(packageName, tag);

        if (null != manifest && CollectionUtils.isNotEmpty(manifest.getLayers())) {
            double imageSize = manifest.getLayers()
                    .stream()
                    .map(DockerLayer::getSize)
                    .reduce((totalSize, currentLayerSize) -> totalSize + currentLayerSize)
                    .get() / BYTES_IN_MB;

            LOGGER.info("Size of {}/{}: {} MB", packageName, tag, imageSize);
            return shieldsIOClient.downloadBadge(String.format("%s-%.2f MB-blue.svg", badgeLabel, imageSize));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    @Cacheable("layers-cache")
    public String getImageLayersBadge(String packageName, String tag, String badgeLabel) {
        DockerManifest manifest = readManifest(packageName, tag);

        if (null != manifest && CollectionUtils.isNotEmpty(manifest.getLayers())) {
            LOGGER.info("Layers in {}/{}: {}", packageName, tag, manifest.getLayers().size());
            return shieldsIOClient.downloadBadge(String.format("%s-%d-blue.svg", badgeLabel,
                    manifest.getLayers().size()));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    @Cacheable("pulls-cache")
    public String getPullsCountBadge(String packageName, String badgeLabel) {
        ArtifactoryFolderInfo folderInfo = artifactoryClient.getFolderInfo(packageName, artifactoryConfig.getApiKey());

        if (null != folderInfo && CollectionUtils.isNotEmpty(folderInfo.getChildren())) {
            long downloadCount = 0;

            for (ArtifactoryFolderElement child : folderInfo.getChildren()) {
                if (child.isFolder()) {
                    ArtifactoryFileStats fileStats = artifactoryClient.getFileStats(
                            packageName + child.getUri() + FILE_NAME_MANIFEST,
                            artifactoryConfig.getApiKey());
                    if (null != fileStats) {
                        downloadCount += fileStats.getDownloadCount();
                    }
                }
            }
            LOGGER.info("Download count of {}: {}", packageName, downloadCount);
            return shieldsIOClient.downloadBadge(String.format("%s-%d-blue.svg", badgeLabel,
                    downloadCount));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    private String generateNotFoundBadge(String badgeLabel) {
        return shieldsIOClient.downloadBadge(String.format("%s-Not Found-blue.svg", badgeLabel));
    }

    private DockerManifest readManifest(String packageName, String tag) {
        String fullPackageName = packageName + "/" + tag;
        String manifestContent = artifactoryClient.getFileContent(
                fullPackageName + FILE_NAME_MANIFEST, artifactoryConfig.getApiKey());

        return readJson(manifestContent, DockerManifest.class);
    }

    private <T> T readJson(String content, Class<T> outputClass) {
        try {
            return objectMapper.readValue(content, outputClass);
        } catch (IOException e) {
            LOGGER.error("Exception when reading content: {}", e.getMessage());
            return null;
        }
    }
}
