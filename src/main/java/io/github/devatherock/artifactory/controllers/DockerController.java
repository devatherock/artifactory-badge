package io.github.devatherock.artifactory.controllers;

import io.github.devatherock.artifactory.service.DockerBadgeService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import lombok.RequiredArgsConstructor;

@Controller("/docker")
@RequiredArgsConstructor
public class DockerController {
    private static final String CONTENT_TYPE_BADGE = "image/svg+xml; charset=utf-8";
    private final DockerBadgeService badgeService;

    @Get(value = "/pulls", produces = CONTENT_TYPE_BADGE)
    public String getImagePullCount(@QueryValue("package") String packageName,
                                    @QueryValue(value = "label", defaultValue = "docker pulls") String badgeLabel) {
        return badgeService.getPullsCountBadge(packageName, badgeLabel);
    }

    @Get(value = "/image-size", produces = CONTENT_TYPE_BADGE)
    public String getImageSize(@QueryValue("package") String packageName,
                               @QueryValue(defaultValue = "latest") String tag,
                               @QueryValue(value = "label", defaultValue = "image size") String badgeLabel) {
        return badgeService.getImageSizeBadge(packageName, tag, badgeLabel);
    }

    @Get(value = "/layers", produces = CONTENT_TYPE_BADGE)
    public String getImageLayers(@QueryValue("package") String packageName,
                                 @QueryValue(defaultValue = "latest") String tag,
                                 @QueryValue(value = "label", defaultValue = "layers") String badgeLabel) {
        return badgeService.getImageLayersBadge(packageName, tag, badgeLabel);
    }
}
