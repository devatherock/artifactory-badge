package io.github.devatherock.artifactory.controllers;

import io.github.devatherock.artifactory.service.DockerBadgeService;
import io.github.devatherock.artifactory.util.ApiSpecConstants;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
     * Generates the version badge
     * 
     * @param packageName
     * @param badgeLabel
     * @param sort
     * @return the version badge
     */
    @Get(value = "/version", produces = CONTENT_TYPE_BADGE)
    @Operation(description = "Generates the version badge", responses = @ApiResponse(description = "An XML representing the SVG version badge", content = @Content(mediaType = CONTENT_TYPE_BADGE, schema = @Schema(implementation = String.class))))
    public String getLatestVersion(
                                   @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_PACKAGE, example = ApiSpecConstants.EXAMPLE_PARAM_PACKAGE) @QueryValue("package") String packageName,
                                   @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_LABEL) @QueryValue(value = "label", defaultValue = "version") String badgeLabel,
                                   @Parameter(in = ParameterIn.QUERY, description = "The attribute based on which to determine the latest version") @QueryValue(value = "sort", defaultValue = "date") String sortType) {
        LOGGER.debug("In getLatestVersion");
        return badgeService.getLatestVersionBadge(packageName, badgeLabel, sortType);
    }
}
