package io.github.devatherock.artifactory.util;

import io.github.devatherock.artifactory.config.ShieldsIOProperties;
import io.github.devatherock.artifactory.service.ShieldsIOClient;
import io.micronaut.core.io.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class BadgeGenerator {
    private static final int WIDTH_ONE_CHAR = 8;

    private final ShieldsIOClient shieldsIOClient;
    private final HttpClient httpClient;
    private final ShieldsIOProperties config;

    @SneakyThrows
    public String generateBadge(String label, String value) {
        String badge = null;

        if (config.isEnabled()) {
            String shieldIoImageName = String.format("%s-%s-blue.svg", label, value);

            if (config.isCustomHttp()) {
                // For URL encoding spaces in the path. URLEncoder.encode encodes them into + instead of %20
                URL url = new URL(config.getUrl() + "/" + shieldIoImageName);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                        url.getQuery(), url.getRef());
                HttpGet httpGet = new HttpGet(uri);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                try (InputStream stream = httpResponse.getEntity().getContent();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    badge = IOUtils.readText(reader);
                }
            } else {
                badge = shieldsIOClient.downloadBadge(shieldIoImageName);
            }
        } else {
            badge = generateSvg(label, value);
        }

        return badge;
    }

    private String generateSvg(String label, String value) {
        LOGGER.debug("Generating custom SVG");
        int labelWidth = (label.length() + 2) * WIDTH_ONE_CHAR;
        int valueWidth = (value.length() + 2) * WIDTH_ONE_CHAR;
        int totalWidth = labelWidth + valueWidth;
        String title = label + ": " + value;

        StringBuilder svg = new StringBuilder();
        svg.append(
                String.format("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"%d\" height=\"20\" role=\"img\" aria-label=\"%s\">",
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
        svg.append(String.format("<rect x=\"%d\" width=\"%d\" height=\"20\" fill=\"#007ec6\"/>",
                labelWidth, valueWidth));
        svg.append(String.format("<rect width=\"%d\" height=\"20\" fill=\"url(#s)\"/>", totalWidth));
        svg.append("</g>");
        svg.append("<g font-family=\"monospace\">");
        svg.append(
                String.format("<text aria-hidden=\"true\" x=\"0\" y=\"15\" fill=\"#fff\" xml:space=\"preserve\"> %s </text>",
                        label));
        svg.append(String.format("<text aria-hidden=\"true\" x=\"%d\" y=\"15\" fill=\"#fff\" xml:space=\"preserve\"> %s </text>",
                labelWidth, value));
        svg.append("</g>");
        svg.append("</svg>");

        return svg.toString();
    }
}
