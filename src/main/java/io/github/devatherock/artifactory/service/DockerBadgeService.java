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
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service class to fetch information required for the badges and then generate
 * them
 *
 * @author devaprasadh
 */
@Slf4j
@Blocking
@Singleton
@RequiredArgsConstructor
public class DockerBadgeService {
    private static final double BYTES_IN_MB = 1024d * 1024;
    private static final String FILE_NAME_MANIFEST = "/manifest.json";
    private static final String HDR_API_KEY = "X-JFrog-Art-Api";
    private static final double PULLS_REDUCER = 1000;
    private static final int MAX_REDUCTIONS = 3;
    private static final Map<Integer, String> PULLS_SUFFIX = new HashMap<>();
    /**
     * Formatter to parse dates like {@code 2020-10-01T00:00:00.000Z}
     */
    private static final DateTimeFormatter MODIFIED_TIME_PARSER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    /**
     * Pattern to match versions like {@code 1}, {@code 1.2} and {@code 1.2.2}
     */
    private static final Pattern PTRN_NUMERIC_VERSION = Pattern.compile("^[0-9]+([\\.0-9]+)*$");

    static {
        PULLS_SUFFIX.put(0, "");
        PULLS_SUFFIX.put(1, "k");
        PULLS_SUFFIX.put(2, "M");
        PULLS_SUFFIX.put(3, "G");
    }

    private final BlockingHttpClient artifactoryClient;
    private final BadgeGenerator badgeGenerator;
    private final ArtifactoryProperties artifactoryConfig;

    @Cacheable(cacheNames = "size-cache")
    public String getImageSizeBadge(String packageName, String tag, String badgeLabel) {
        LOGGER.debug("In getImageSizeBadge");
        DockerManifest manifest = readManifest(packageName, tag);

        if (null != manifest && CollectionUtils.isNotEmpty(manifest.getLayers())) {
            double imageSize = manifest.getLayers().stream().map(DockerLayer::getSize)
                    .reduce((totalSize, currentLayerSize) -> totalSize + currentLayerSize).get() / BYTES_IN_MB;

            LOGGER.info("Size of {}/{}: {} MB", packageName, tag, imageSize);
            return badgeGenerator.generateBadge(badgeLabel, String.format("%s MB", formatDecimal(imageSize, "0.##")));
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
        ArtifactoryFolderInfo folderInfo = getArtifactoryFolderInfo(packageName);

        if (null != folderInfo && CollectionUtils.isNotEmpty(folderInfo.getChildren())) {
            long downloadCount = 0;

            for (ArtifactoryFolderElement child : folderInfo.getChildren()) {
                if (child.isFolder()) {
                    ArtifactoryFileStats fileStats = getManifestStats(packageName, child.getUri());

                    if (null != fileStats) {
                        downloadCount += fileStats.getDownloadCount();
                    }
                }
            }
            LOGGER.info("Download count of {}: {}", packageName, downloadCount);
            return badgeGenerator.generateBadge(badgeLabel, formatDownloadCount(downloadCount));
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    @Cacheable("version-cache")
    public String getLatestVersionBadge(String packageName, String badgeLabel) {
        LOGGER.debug("In getLatestVersionBadge");
        ArtifactoryFolderInfo folderInfo = getArtifactoryFolderInfo(packageName);

        if (null != folderInfo && CollectionUtils.isNotEmpty(folderInfo.getChildren())) {
            ArtifactoryFolderInfo latestVersion = null;

            for (ArtifactoryFolderElement child : folderInfo.getChildren()) {
                if (child.isFolder()) {
                    ArtifactoryFolderInfo currentVersion = getArtifactoryFolderInfo(packageName + child.getUri());

                    if (null == latestVersion || (null != currentVersion
                            && Instant.from(MODIFIED_TIME_PARSER.parse(currentVersion.getLastModified())).compareTo(
                                    Instant.from(MODIFIED_TIME_PARSER.parse(latestVersion.getLastModified()))) > 0)) {
                        latestVersion = currentVersion;
                    }
                }
            }

            if (null != latestVersion) {
                LOGGER.info("Latest version of {}: {}", packageName, latestVersion.getPath());
                return badgeGenerator.generateBadge(badgeLabel, getVersionBadgeValue(latestVersion));
            } else {
                return generateNotFoundBadge(badgeLabel);
            }
        } else {
            return generateNotFoundBadge(badgeLabel);
        }
    }

    /**
     * Gets folder information from artifactory
     *
     * @param packageName the package name
     * @return {@link ArtifactoryFolderInfo}
     */
    private ArtifactoryFolderInfo getArtifactoryFolderInfo(String packageName) {
        ArtifactoryFolderInfo folderInfo = null;
        HttpRequest<Object> folderRequest = HttpRequest
                .create(HttpMethod.GET, artifactoryConfig.getStorageUrlPrefix() + packageName)
                .header(HDR_API_KEY, artifactoryConfig.getApiKey());

        try {
            folderInfo = artifactoryClient.retrieve(folderRequest, ArtifactoryFolderInfo.class);
        } catch (HttpClientResponseException exception) {
            LOGGER.warn("Exception when fetching folder info of {}", packageName, exception);
        }

        return folderInfo;
    }

    /**
     * Generates a not found badge
     *
     * @param badgeLabel the badge label
     * @return a not found badge
     */
    private String generateNotFoundBadge(String badgeLabel) {
        return badgeGenerator.generateBadge(badgeLabel, "Not Found");
    }

    /**
     * Reads a docker {@code manifest.json} file
     *
     * @param packageName the docker image name
     * @param tag         the docker image tag
     * @return a {@link DockerManifest}
     */
    private DockerManifest readManifest(String packageName, String tag) {
        String fullPackageName = packageName + "/" + tag;
        DockerManifest manifest = null;
        HttpRequest<Object> manifestRequest = HttpRequest
                .create(HttpMethod.GET, artifactoryConfig.getUrlPrefix() + fullPackageName + FILE_NAME_MANIFEST)
                .header(HDR_API_KEY, artifactoryConfig.getApiKey());

        try {
            manifest = artifactoryClient.retrieve(manifestRequest, DockerManifest.class);
        } catch (HttpClientResponseException exception) {
            LOGGER.warn("Exception when reading manifest.json of {}", fullPackageName, exception);
        }

        return manifest;
    }

    /**
     * Reads statistics of the {@code manifest.json} file for the supplied docker
     * image and tag
     * 
     * @param packageName the docker image name
     * @param tagUri      subfolder path to a docker image tag
     * @return {@link ArtifactoryFileStats}
     */
    private ArtifactoryFileStats getManifestStats(String packageName, String tagUri) {
        ArtifactoryFileStats fileStats = null;
        HttpRequest<Object> fileRequest = HttpRequest
                .create(HttpMethod.GET,
                        artifactoryConfig.getStorageUrlPrefix() + packageName + tagUri + FILE_NAME_MANIFEST + "?stats")
                .header(HDR_API_KEY, artifactoryConfig.getApiKey());

        try {
            fileStats = artifactoryClient.retrieve(fileRequest, ArtifactoryFileStats.class);
        } catch (HttpClientResponseException exception) {
            LOGGER.warn("Exception when reading manifest stats of {}{}", packageName, tagUri, exception);
        }

        return fileStats;
    }

    /**
     * Returns the formatted value to be displayed in the version badge
     *
     * @param version
     * @return the version badge value
     */
    private String getVersionBadgeValue(ArtifactoryFolderInfo version) {
        String versionValue = version.getPath().substring(version.getPath().lastIndexOf('/') + 1);

        // Append v prefix if version is numeric or a semantic version
        return PTRN_NUMERIC_VERSION.matcher(versionValue).matches() ? 'v' + versionValue : versionValue;
    }

    /**
     * Formats a decimal number into a string
     *
     * @param inputDecimal the decimal number to format
     * @param format       the pattern to which to format to
     * @return a formatted string
     */
    private String formatDecimal(double inputDecimal, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        return decimalFormat.format(inputDecimal);
    }

    /**
     * Formats the download count with a suffix
     *
     * @param downloadCount number of downloads
     * @return a formatted string
     */
    private String formatDownloadCount(long downloadCount) {
        double reducedCount = downloadCount;
        int reductions = 0;

        while (reducedCount > PULLS_REDUCER && reductions < MAX_REDUCTIONS) {
            reductions++;
            reducedCount = reducedCount / PULLS_REDUCER;
        }

        return formatDecimal(reducedCount, "0.#") + PULLS_SUFFIX.get(reductions);
    }
}
