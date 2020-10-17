package io.github.devatherock.artifactory.service;

import io.github.devatherock.artifactory.config.ArtifactoryProperties;
import io.github.devatherock.artifactory.entities.ArtifactoryFileStats;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderElement;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;
import io.github.devatherock.artifactory.entities.DockerLayer;
import io.github.devatherock.artifactory.entities.DockerManifest;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class DockerBadgeService {
    private static final double BYTES_IN_MB = 1024 * 1024;
    private static final String FILE_NAME_MANIFEST = "/manifest.json";
    private static final String HDR_API_KEY = "X-JFrog-Art-Api";

    private final BlockingHttpClient artifactoryClient;
    private final ShieldsIOClient shieldsIOClient;
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
        HttpRequest<Object> folderRequest = HttpRequest.create(HttpMethod.GET, artifactoryConfig.getStorageUrlPrefix()
                + packageName).header(HDR_API_KEY, artifactoryConfig.getApiKey());
        ArtifactoryFolderInfo folderInfo = artifactoryClient.retrieve(folderRequest, ArtifactoryFolderInfo.class);

        if (null != folderInfo && CollectionUtils.isNotEmpty(folderInfo.getChildren())) {
            long downloadCount = 0;

            for (ArtifactoryFolderElement child : folderInfo.getChildren()) {
                if (child.isFolder()) {
                    HttpRequest<Object> fileRequest = HttpRequest.create(HttpMethod.GET, artifactoryConfig.getStorageUrlPrefix()
                            + packageName + child.getUri() + FILE_NAME_MANIFEST).header(HDR_API_KEY, artifactoryConfig.getApiKey());
                    ArtifactoryFileStats fileStats = artifactoryClient.retrieve(fileRequest, ArtifactoryFileStats.class);

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
        HttpRequest<Object> manifestRequest = HttpRequest.create(HttpMethod.GET, artifactoryConfig.getUrlPrefix()
                + fullPackageName).header(HDR_API_KEY, artifactoryConfig.getApiKey());
        return artifactoryClient.retrieve(manifestRequest, DockerManifest.class);
    }
}
