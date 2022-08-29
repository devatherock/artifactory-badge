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
     * Generates the docker pulls badge
     * 
     * @param packageName
     * @param badgeLabel
     * @return the pulls badge
     */
    @Get(value = "/pulls", produces = CONTENT_TYPE_BADGE)
    @Operation(description = "Generates the docker pulls badge", responses = @ApiResponse(description = "An XML representing the SVG docker pulls badge", content = @Content(mediaType = CONTENT_TYPE_BADGE, schema = @Schema(implementation = String.class))))
    public String getImagePullCount(
                                    @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_PACKAGE, example = ApiSpecConstants.EXAMPLE_PARAM_PACKAGE) @QueryValue("package") String packageName,
                                    @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_LABEL) @QueryValue(value = "label", defaultValue = "docker pulls") String badgeLabel) {
        LOGGER.debug("In getImagePullCount");
        return badgeService.getPullsCountBadge(packageName, badgeLabel);
    }

    /**
     * Generates the image size badge
     * 
     * @param packageName
     * @param tag
     * @param badgeLabel
     * @return the image size badge
     */
    @Get(uris = { "/image-size", "image_size" }, produces = CONTENT_TYPE_BADGE)
    @Operation(description = "Generates the image size badge", responses = @ApiResponse(description = "An XML representing the SVG image size badge", content = @Content(mediaType = CONTENT_TYPE_BADGE, schema = @Schema(implementation = String.class))))
    public String getImageSize(
                               @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_PACKAGE, example = ApiSpecConstants.EXAMPLE_PARAM_PACKAGE) @QueryValue("package") String packageName,
                               @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_TAG) @QueryValue(defaultValue = "latest") String tag,
                               @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_LABEL) @QueryValue(value = "label", defaultValue = "image size") String badgeLabel) {
        LOGGER.debug("In getImageSize");
        return badgeService.getImageSizeBadge(packageName, tag, badgeLabel);
    }

    /**
     * Generates the layers badge
     * 
     * @param packageName
     * @param tag
     * @param badgeLabel
     * @return the layers badge
     */
    @Get(value = "/layers", produces = CONTENT_TYPE_BADGE)
    @Operation(description = "Generates the layers badge", responses = @ApiResponse(description = "An XML representing the SVG layers badge", content = @Content(mediaType = CONTENT_TYPE_BADGE, schema = @Schema(implementation = String.class))))
    public String getImageLayers(
                                 @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_PACKAGE, example = ApiSpecConstants.EXAMPLE_PARAM_PACKAGE) @QueryValue("package") String packageName,
                                 @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_TAG) @QueryValue(defaultValue = "latest") String tag,
                                 @Parameter(in = ParameterIn.QUERY, description = ApiSpecConstants.API_DESC_PARAM_LABEL) @QueryValue(value = "label", defaultValue = "layers") String badgeLabel) {
        LOGGER.debug("In getImageLayers");
        return badgeService.getImageLayersBadge(packageName, tag, badgeLabel);
    }
}
