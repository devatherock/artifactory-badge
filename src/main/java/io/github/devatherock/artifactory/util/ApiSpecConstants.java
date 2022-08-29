package io.github.devatherock.artifactory.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiSpecConstants {
    public static final String API_DESC_PARAM_PACKAGE = "The full path to the docker image within the artifactory docker registry";
    public static final String API_DESC_PARAM_LABEL = "The label to use in the generated badge";
    public static final String API_DESC_PARAM_TAG = "The docker image tag for which to generate the badge";
    public static final String EXAMPLE_PARAM_PACKAGE = "docker/devatherock/drone-yaml-validator";
}
