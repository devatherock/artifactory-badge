package io.github.devatherock.artifactory.controllers;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;

import java.util.Iterator;
import java.util.Map;

import io.github.devatherock.test.BaseSimulation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

public class CompositeSimulation extends BaseSimulation {

    {
        setUp(buildScenario().injectOpen(
                rampUsers(Integer.parseInt(getConfig("perf.users")))
                        .during(1)))
                .protocols(buildProtocol())
                .maxDuration(Long.parseLong(getConfig("perf.duration")))
                .assertions(
                        global().responseTime().percentile(95).lt(2500),
                        global().successfulRequests().percent().is(100d));
    }

    protected ScenarioBuilder buildScenario() {
        Iterator<Map<String, Object>> feeder = createImageNameFeeder();

        ChainBuilder suite = exec(
                DockerPullsSimulation.buildRequest(),
                VersionSimulation.buildRequest());

        return scenario("All APIs")
                .feed(feeder)
                .forever()
                .on(exec(suite).feed(feeder));
    }
}
