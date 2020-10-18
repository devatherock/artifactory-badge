package io.github.devatherock.artifactory.util;

import io.github.devatherock.artifactory.config.ArtifactoryBadgeProperties;
import io.github.devatherock.artifactory.service.ShieldsIOClient;
import io.micronaut.core.io.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

@Singleton
@RequiredArgsConstructor
public class BadgeGenerator {
    private static final float WIDTH_ONE_CHAR = 5.8f;
    private static final float TEXT_LENGTH_MULTIPLIER = 8.5f;
    private final ShieldsIOClient shieldsIOClient;
    private final HttpClient httpClient;
    private final ArtifactoryBadgeProperties config;

    @SneakyThrows
    public String generateBadge(String label, String value) {
        String badge = null;

        if (config.getShieldsIo().isEnabled()) {
            String shieldIoImageName = String.format("%s-%s-blue.svg", label, value);

            if (config.getShieldsIo().isCustomHttp()) {
                // For URL encoding spaces in the path. URLEncoder.encode encodes them into + instead of %20
                URL url = new URL(config.getShieldsIo().getUrl() + "/" + shieldIoImageName);
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
        float labelWidthFloat = (label.length() + 2) * WIDTH_ONE_CHAR;
        float valueWidthFloat = (value.length() + 2) * WIDTH_ONE_CHAR;
        float labelLengthFloat = labelWidthFloat * TEXT_LENGTH_MULTIPLIER;
        float valueLengthFloat = valueWidthFloat * TEXT_LENGTH_MULTIPLIER;

        String title = label + ": " + value;
        int labelWidth = (int) (labelWidthFloat);
        int valueWidth = (int) (valueWidthFloat);
        int totalWidth = labelWidth + valueWidth;
        int labelLength = (int) labelLengthFloat;
        int valueLength = (int) valueLengthFloat;
        int labelStartPosition = (int) ((labelLengthFloat / 2) + (WIDTH_ONE_CHAR * TEXT_LENGTH_MULTIPLIER));
        int valueStartPosition = (int) (labelLengthFloat + (valueLengthFloat * 1.7 / 2));
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
        svg.append("<g fill=\"#fff\" text-anchor=\"middle\" font-family=\"Verdana,Geneva,DejaVu Sans,sans-serif\" text-rendering=\"geometricPrecision\" font-size=\"110\">");
        svg.append(
                String.format("<text aria-hidden=\"true\" x=\"%d\" y=\"150\" fill=\"#010101\" fill-opacity=\".3\" transform=\"scale(.1)\" textLength=\"%d\">%s</text>",
                        labelStartPosition, labelLength, label));
        svg.append(String.format("<text x=\"%d\" y=\"140\" transform=\"scale(.1)\" fill=\"#fff\" textLength=\"%d\">%s</text>",
                labelStartPosition, labelLength, label));
        svg.append(
                String.format("<text aria-hidden=\"true\" x=\"%d\" y=\"150\" fill=\"#010101\" fill-opacity=\".3\" transform=\"scale(.1)\" textLength=\"%d\">%s</text>",
                        valueStartPosition, valueLength, value));
        svg.append(String.format("<text x=\"%d\" y=\"140\" transform=\"scale(.1)\" fill=\"#fff\" textLength=\"%d\">%s</text>",
                valueStartPosition, valueLength, value));
        svg.append("</g>");
        svg.append("</svg>");

        return svg.toString();
    }
}
