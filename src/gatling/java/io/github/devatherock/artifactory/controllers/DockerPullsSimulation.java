package io.github.devatherock.artifactory.controllers;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.devatherock.test.BaseSimulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

/**
 * Perf tests for the {@code docker/pulls} endpoint
 */
public class DockerPullsSimulation extends BaseSimulation {
    private static final String GET_PULLS = "GET /docker/pulls";

    {
        setUp(buildScenario().injectOpen(
                rampUsers(Integer.parseInt(getConfig("perf.docker-pulls.users")))
                        .during(1)))
                .protocols(buildProtocol())
                .maxDuration(Long.parseLong(getConfig("perf.docker-pulls.duration")));
    }

    protected ScenarioBuilder buildScenario() {
        Iterator<Map<String, Object>> feeder = Stream.generate(
                (Supplier<Map<String, Object>>) () -> Collections.singletonMap("imageName", generateRandomString()))
                .iterator();

        HttpRequestActionBuilder request = http(GET_PULLS)
                .get("/docker/pulls?package=devatherock/#{imageName}")
                .check(status().is(200));

        return scenario(GET_PULLS)
                .feed(feeder)
                .forever()
                .on(exec(request).feed(feeder));
    }
}
