package io.github.devatherock.artifactory.util;

import io.github.devatherock.artifactory.config.ShieldsIOProperties;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

/**
 * Class to generate the badges
 *
 * @author devaprasadh
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class BadgeGenerator {
    private static final int WIDTH_ONE_CHAR = 8;

    private final BlockingHttpClient shieldsIOClient;
    private final ShieldsIOProperties config;

    /**
     * Generates a badge with the supplied label and value using
     * {@code shields.io} or custom logic
     *
     * @param label
     * @param value
     * @return a badge
     */
    public String generateBadge(String label, String value) {
        LOGGER.debug("In generateBadge");
        String badge = null;

        if (config.isEnabled()) {
            try {
                HttpRequest<Object> badgeRequest = HttpRequest.GET(UriBuilder.of(config.getBadgeUrl())
                        .queryParam("label", label)
                        .queryParam("message", value)
                        .queryParam("color", "blue").build());
                badge = shieldsIOClient.retrieve(badgeRequest, String.class);
            } catch (HttpClientResponseException exception) {
                LOGGER.warn("Exception from shields.io when generating badge for label={}, value={}", label, value,
                        exception);
                badge = generateSvg(label, value);
            }
        } else {
            badge = generateSvg(label, value);
        }

        return badge;
    }

    /**
     * Generates a custom badge with the supplied label and value
     *
     * @param label
     * @param value
     * @return a badge
     */
    private String generateSvg(String label, String value) {
        LOGGER.debug("Generating custom SVG");
        int labelWidth = (label.length() + 2) * WIDTH_ONE_CHAR;
        int valueWidth = (value.length() + 2) * WIDTH_ONE_CHAR;
        int totalWidth = labelWidth + valueWidth;
        String title = label + ": " + value;

        StringBuilder svg = new StringBuilder();
        svg.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"%d\" height=\"20\" role=\"img\" aria-label=\"%s\">",
                totalWidth, title));
        svg.append("<title>");
        svg.append(title);
        svg.append("</title>");
        svg.append("<linearGradient id=\"s\" x2=\"0\" y2=\"100%\">");
        svg.append("<stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/>");
        svg.append("<stop offset=\"1\" stop-opacity=\".1\"/>");
        svg.append("</linearGradient>");
        svg.append("<clipPath id=\"r\">");
        svg.append(String.format("<rect width=\"%d\" height=\"20\" rx=\"3\" fill=\"#fff\"/>", totalWidth));
        svg.append("</clipPath>");
        svg.append("<g clip-path=\"url(#r)\">");
        svg.append(String.format("<rect width=\"%d\" height=\"20\" fill=\"#555\"/>", labelWidth));
        svg.append(
                String.format("<rect x=\"%d\" width=\"%d\" height=\"20\" fill=\"#007ec6\"/>", labelWidth, valueWidth));
        svg.append(String.format("<rect width=\"%d\" height=\"20\" fill=\"url(#s)\"/>", totalWidth));
        svg.append("</g>");
        svg.append("<g font-family=\"monospace\">");
        svg.append(String.format(
                "<text aria-hidden=\"true\" x=\"0\" y=\"15\" fill=\"#fff\" xml:space=\"preserve\"> %s </text>", label));
        svg.append(String.format(
                "<text aria-hidden=\"true\" x=\"%d\" y=\"15\" fill=\"#fff\" xml:space=\"preserve\"> %s </text>",
                labelWidth, value));
        svg.append("</g>");
        svg.append("</svg>");

        return svg.toString();
    }
}
