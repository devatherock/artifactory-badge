package io.github.devatherock.artifactory.service;

import io.github.devatherock.artifactory.config.ArtifactoryProperties;
import io.github.devatherock.artifactory.entities.ArtifactoryFileStats;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderElement;
import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;
import io.github.devatherock.artifactory.entities.DockerLayer;
import io.github.devatherock.artifactory.entities.DockerManifest;
import io.github.devatherock.artifactory.util.BadgeGenerator;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.Blocking;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Blocking
@Singleton
public class DockerBadgeService {
    private static final double BYTES_IN_MB = 1024 * 1024;
    private static final String FILE_NAME_MANIFEST = "/manifest.json";
    private static final String HDR_API_KEY = "X-JFrog-Art-Api";

    private final BlockingHttpClient artifactoryClient;
    private final BadgeGenerator badgeGenerator;
    private final ArtifactoryProperties artifactoryConfig;

    @Inject
    public DockerBadgeService(@Client("${artifactory.url}") HttpClient artifactoryClient,
                              BadgeGenerator badgeGenerator,
                              ArtifactoryProperties artifactoryConfig) {
        this.artifactoryClient = artifactoryClient.toBlocking();
        this.badgeGenerator = badgeGenerator;
        this.artifactoryConfig = artifactoryConfig;
    }

    @Cacheable(cacheNames = "size-cache")
    public String getImageSizeBadge(String packageName, String tag, String badgeLabel) {
        LOGGER.debug("In getImageSizeBadge");
        DockerManifest manifest = readManifest(packageName, tag);

        if (null != manifest && CollectionUtils.isNotEmpty(manifest.getLayers())) {
            double imageSize = manifest.getLayers()
                    .stream()
                    .map(DockerLayer::getSize)
                    .reduce((totalSize, currentLayerSize) -> totalSize + currentLayerSize)
                    .get() / BYTES_IN_MB;

            LOGGER.info("Size of {}/{}: {} MB", packageName, tag, imageSize);
            return badgeGenerator.generateBadge(badgeLabel, String.format("%.2f MB", imageSize));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    @Cacheable("layers-cache")
    public String getImageLayersBadge(String packageName, String tag, String badgeLabel) {
        LOGGER.debug("In getImageLayersBadge");
        DockerManifest manifest = readManifest(packageName, tag);

        if (null != manifest && CollectionUtils.isNotEmpty(manifest.getLayers())) {
            LOGGER.info("Layers in {}/{}: {}", packageName, tag, manifest.getLayers().size());
            return badgeGenerator.generateBadge(badgeLabel, String.valueOf(manifest.getLayers().size()));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    @Cacheable("pulls-cache")
    public String getPullsCountBadge(String packageName, String badgeLabel) {
        LOGGER.debug("In getPullsCountBadge");
        HttpRequest<Object> folderRequest = HttpRequest.create(HttpMethod.GET, artifactoryConfig.getStorageUrlPrefix()
                + packageName).header(HDR_API_KEY, artifactoryConfig.getApiKey());
        ArtifactoryFolderInfo folderInfo = artifactoryClient.retrieve(folderRequest, ArtifactoryFolderInfo.class);

        if (null != folderInfo && CollectionUtils.isNotEmpty(folderInfo.getChildren())) {
            long downloadCount = 0;

            for (ArtifactoryFolderElement child : folderInfo.getChildren()) {
                if (child.isFolder()) {
                    HttpRequest<Object> fileRequest = HttpRequest.create(HttpMethod.GET,
                            artifactoryConfig.getStorageUrlPrefix() + packageName
                                    + child.getUri() + FILE_NAME_MANIFEST + "?stats")
                            .header(HDR_API_KEY, artifactoryConfig.getApiKey());
                    ArtifactoryFileStats fileStats = artifactoryClient.retrieve(fileRequest, ArtifactoryFileStats.class);

                    if (null != fileStats) {
                        downloadCount += fileStats.getDownloadCount();
                    }
                }
            }
            LOGGER.info("Download count of {}: {}", packageName, downloadCount);
            return badgeGenerator.generateBadge(badgeLabel, String.valueOf(downloadCount));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    private String generateNotFoundBadge(String badgeLabel) {
        return badgeGenerator.generateBadge(badgeLabel, "Not Found");
    }

    private DockerManifest readManifest(String packageName, String tag) {
        String fullPackageName = packageName + "/" + tag;
        HttpRequest<Object> manifestRequest = HttpRequest.create(HttpMethod.GET, artifactoryConfig.getUrlPrefix()
                + fullPackageName + FILE_NAME_MANIFEST).header(HDR_API_KEY, artifactoryConfig.getApiKey());
        return artifactoryClient.retrieve(manifestRequest, DockerManifest.class);
    }
}
