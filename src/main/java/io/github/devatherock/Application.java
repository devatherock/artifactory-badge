package io.github.devatherock;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "artifactory-badge", version = "1.0.0"), servers = {
        @Server(url = "http://localhost:8080", description = "The server where the application is hosted. Defaulted to localhost")
})
public class Application {
    private static final String ENV_LOGBACK_CONFIG = "LOGBACK_CONFIGURATION_FILE";

    public static void main(String[] args) {
        if (System.getProperty(ContextInitializer.CONFIG_FILE_PROPERTY) == null &&
                System.getenv(ENV_LOGBACK_CONFIG) != null) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                context.reset();
                configurator.doConfigure(
                        Application.class.getClassLoader().getResourceAsStream(System.getenv(
                                ENV_LOGBACK_CONFIG)));
            } catch (JoranException je) {
                // StatusPrinter will handle this
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        }

        Micronaut.run(Application.class);
    }
}