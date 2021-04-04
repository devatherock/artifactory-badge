package io.github.devatherock.artifactory.controllers;

import io.github.devatherock.artifactory.service.DockerBadgeService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to generate badges for docker images
 * 
 * @author devaprasadh
 *
 */
@Slf4j
@ExecuteOn(TaskExecutors.IO)
@Controller("/docker")
@RequiredArgsConstructor
public class DockerController {
    private static final String CONTENT_TYPE_BADGE = "image/svg+xml; charset=utf-8";
    private final DockerBadgeService badgeService;

    /**
     * Generates the {@code docker pulls} badge
     * 
     * @param packageName
     * @param badgeLabel
     * @return the pulls badge
     */
    @Get(value = "/pulls", produces = CONTENT_TYPE_BADGE)
    public String getImagePullCount(@QueryValue("package") String packageName,
                                    @QueryValue(value = "label", defaultValue = "docker pulls") String badgeLabel) {
        LOGGER.debug("In getImagePullCount");
        return badgeService.getPullsCountBadge(packageName, badgeLabel);
    }

    /**
     * Generates the {@code image size} badge
     * 
     * @param packageName
     * @param tag
     * @param badgeLabel
     * @return the image size badge
     */
    @Get(value = "/image-size", produces = CONTENT_TYPE_BADGE)
    public String getImageSize(@QueryValue("package") String packageName,
                               @QueryValue(defaultValue = "latest") String tag,
                               @QueryValue(value = "label", defaultValue = "image size") String badgeLabel) {
        LOGGER.debug("In getImageSize");
        return badgeService.getImageSizeBadge(packageName, tag, badgeLabel);
    }

    /**
     * Generates the {@code layers} badge
     * 
     * @param packageName
     * @param tag
     * @param badgeLabel
     * @return the layers badge
     */
    @Get(value = "/layers", produces = CONTENT_TYPE_BADGE)
    public String getImageLayers(@QueryValue("package") String packageName,
                                 @QueryValue(defaultValue = "latest") String tag,
                                 @QueryValue(value = "label", defaultValue = "layers") String badgeLabel) {
        LOGGER.debug("In getImageLayers");
        return badgeService.getImageLayersBadge(packageName, tag, badgeLabel);
    }
}
