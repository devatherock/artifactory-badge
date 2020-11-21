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
 * Controller to generate version badge, for docker images and jar files
 * 
 * @author devaprasadh
 *
 */
@Slf4j
@ExecuteOn(TaskExecutors.IO)
@Controller
@RequiredArgsConstructor
public class VersionController {
    private static final String CONTENT_TYPE_BADGE = "image/svg+xml; charset=utf-8";
    private final DockerBadgeService badgeService;

    /**
     * Generates the {@code version} badge
     * 
     * @param packageName
     * @param badgeLabel
     * @param sort
     * @return the version badge
     */
    @Get(value = "/version", produces = CONTENT_TYPE_BADGE)
    public String getLatestVersion(@QueryValue("package") String packageName,
                                   @QueryValue(value = "label", defaultValue = "version") String badgeLabel,
                                   @QueryValue(value = "sort", defaultValue = "date") String sortType) {
        LOGGER.debug("In getLatestVersion");
        return badgeService.getLatestVersionBadge(packageName, badgeLabel, sortType);
    }
}
