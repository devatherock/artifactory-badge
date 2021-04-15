package io.github.devatherock.artifactory.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.devatherock.artifactory.entities.ArtifactoryFolderInfo;

import io.micronaut.core.annotation.Blocking;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper class for {@link DockerBadgeService}
 *
 * @author devaprasadh
 */
@Blocking
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DockerBadgeServiceHelper {
    private static final double PULLS_REDUCER = 1000;
    private static final int MAX_REDUCTIONS = 3;

    /**
     * Major version part of a semantic version
     */
    private static final String VERSION_PART_MAJOR = "major";
    /**
     * Minor version part of a semantic version
     */
    private static final String VERSION_PART_MINOR = "minor";
    /**
     * Patch version part of a semantic version
     */
    private static final String VERSION_PART_PATCH = "patch";
    /**
     * Map containing suffixes for download count
     */
    private static final Map<Integer, String> PULLS_SUFFIX = new HashMap<>();
    /**
     * Pattern to match versions like {@code 1}, {@code 1.2} and {@code 1.2.2}
     */
    private static final Pattern PTRN_NUMERIC_VERSION = Pattern.compile(
            "^(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*))*(\\-[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$");
    /**
     * Pattern to match numbers
     */
    private static final Pattern PTRN_NUMBER = Pattern.compile("^[0-9]+$");

    static {
        PULLS_SUFFIX.put(0, "");
        PULLS_SUFFIX.put(1, "k");
        PULLS_SUFFIX.put(2, "M");
        PULLS_SUFFIX.put(3, "G");
    }

    /**
     * Compares two versions
     *
     * @param versionOne
     * @param versionTwo
     * @param versionPartType
     * @return {@literal -1} if {@code versionTwo} greater than {@code versionOne},
     *         {@literal 1} otherwise
     */
    static int compareVersions(String versionOne, String versionTwo, String versionPartType) {
        int result = 0;

        int versionPartEndOne = getVersionPartEndIndex(versionOne);
        String versionPartOneText = versionOne.substring(0,
                versionPartEndOne != -1 ? versionPartEndOne : versionOne.length());
        long versionPartOne = readVersionAsNumber(versionPartOneText);
        int versionPartEndTwo = getVersionPartEndIndex(versionTwo);
        String versionPartTwoText = versionTwo.substring(0,
                versionPartEndTwo != -1 ? versionPartEndTwo : versionTwo.length());
        long versionPartTwo = readVersionAsNumber(versionPartTwoText);

        if (versionPartOne > versionPartTwo) {
            result = 1;
        } else if (versionPartOne < versionPartTwo) {
            result = -1;
        } else {
            if ((versionPartOneText.length() + 1) >= versionOne.length()) {
                if ((versionPartTwoText.length() + 1) < versionTwo.length()) {
                    result = -1;
                }
            } else if ((versionPartTwoText.length() + 1) < versionTwo.length()) {
                if (!VERSION_PART_PATCH.equals(versionPartType)) {
                    result = compareVersions(versionOne.substring(versionPartEndOne + 1),
                            versionTwo.substring(versionPartEndTwo + 1),
                            VERSION_PART_MAJOR.equals(versionPartType) ? VERSION_PART_MINOR : VERSION_PART_PATCH);
                }
            } else {
                result = 1;
            }
        }

        return result;
    }

    /**
     * Returns the formatted value to be displayed in the version badge
     *
     * @param version
     * @return the version badge value
     */
    static String getVersionBadgeValue(ArtifactoryFolderInfo version) {
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
    static String formatDecimal(double inputDecimal, String format) {
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
    static String formatDownloadCount(long downloadCount) {
        double reducedCount = downloadCount;
        int reductions = 0;

        while (reducedCount > PULLS_REDUCER && reductions < MAX_REDUCTIONS) {
            reductions++;
            reducedCount = reducedCount / PULLS_REDUCER;
        }

        return formatDecimal(reducedCount, "0.#") + PULLS_SUFFIX.get(reductions);
    }

    /**
     * Returns the index at which the first version part ends
     *
     * @param version
     * @return the index
     */
    private static int getVersionPartEndIndex(String version) {
        return version.indexOf('.') != -1 ? version.indexOf('.') : version.indexOf('-');
    }

    /**
     * Converts version string into a number
     *
     * @param version
     * @return the version as number
     */
    private static long readVersionAsNumber(String version) {
        return PTRN_NUMBER.matcher(version).matches() ? Long.parseLong(version) : 0;
    }
}
