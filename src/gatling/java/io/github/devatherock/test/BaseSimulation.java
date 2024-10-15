package io.github.devatherock.test;

import static io.gatling.javaapi.http.HttpDsl.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public abstract class BaseSimulation extends Simulation {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSimulation.class);
    private Map<String, String> config;

    protected HttpProtocolBuilder buildProtocol() {
        return http.baseUrl(getConfig("perf.base.url", "http://localhost:8080"))
                .acceptHeader("*/*");
    }

    protected String getConfig(String name, String defaultValue) {
        String configValue = getConfig(name);

        return configValue == null ? defaultValue : configValue;
    }

    protected String getConfig(String name) {
        if (null == config) {
            config = new ConcurrentHashMap<>();

            try (InputStream stream = BaseSimulation.class.getClassLoader()
                    .getResourceAsStream("application-perf.properties")) {
                if (stream != null) {
                    Properties properties = new Properties();
                    properties.load(stream);

                    properties.forEach((key, value) -> config.put(key.toString(), value.toString()));
                }
            } catch (IOException e) {
                LOGGER.warn("Exception when reading config", e);
            }
        }

        return System.getProperty(name, config.get(name));
    }

    public static String generateRandomString() {
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int index = 0; index < 10; index++) {
            randomString.append((char) random.nextInt('a', 'z'));
        }

        return randomString.toString();
    }

    public static Iterator<Map<String, Object>> createImageNameFeeder() {
        return Stream.generate(
                (Supplier<Map<String, Object>>) () -> Collections.singletonMap("imageName", generateRandomString()))
                .iterator();
    }

    protected abstract ScenarioBuilder buildScenario();
}
