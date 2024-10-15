package io.github.devatherock.artifactory.controllers;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.util.Iterator;
import java.util.Map;

import io.github.devatherock.test.BaseSimulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

/**
 * Perf tests for the {@code /version} endpoint
 */
public class VersionSimulation extends BaseSimulation {
    static final String GET_VERSION = "GET /version";

    {
        setUp(buildScenario().injectOpen(
                rampUsers(Integer.parseInt(getConfig("perf.version.users")))
                        .during(1)))
                .protocols(buildProtocol())
                .maxDuration(Long.parseLong(getConfig("perf.version.duration")));
    }

    protected ScenarioBuilder buildScenario() {
        Iterator<Map<String, Object>> feeder = createImageNameFeeder();

        return scenario(GET_VERSION)
                .feed(feeder)
                .forever()
                .on(exec(buildRequest()).feed(feeder));
    }

    static HttpRequestActionBuilder buildRequest() {
        return http(GET_VERSION)
                .get("/version?package=devatherock/#{imageName}")
                .check(status().is(200));
    }
}
