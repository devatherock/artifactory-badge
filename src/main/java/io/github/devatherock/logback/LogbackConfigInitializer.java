package io.github.devatherock.logback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.LoggerFactory;

import io.github.devatherock.Application;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import lombok.experimental.UtilityClass;

/**
 * Initializes logging config using an environment variable. Needed for native
 * images
 * 
 * @author devaprasadh
 *
 */
@UtilityClass
public class LogbackConfigInitializer {
    private static final String ENV_LOGBACK_CONFIG = "LOGBACK_CONFIGURATION_FILE";
    private static final String PROP_LOGGING_CONFIG = "logger.config";

    /**
     * Initializes logging config using an environment variable
     * 
     * @throws IOException
     */
    public static void initializeConfig() throws IOException {
        if (System.getProperty(ContextInitializer.CONFIG_FILE_PROPERTY) == null &&
                System.getenv(ENV_LOGBACK_CONFIG) != null) {
            initializeConfig(System.getenv(ENV_LOGBACK_CONFIG));
        }
    }

    /**
     * Initializes logging config using the specified config file
     * 
     * @throws IOException
     */
    private static void initializeConfig(String configFile) throws IOException {
        InputStream config = readConfig(configFile);

        if (null != config) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                context.reset();
                configurator.doConfigure(config);
            } catch (JoranException exception) {
                // StatusPrinter will handle logging the exception
                if (configFile.equals(System.getProperty(PROP_LOGGING_CONFIG))) {
                    System.clearProperty(PROP_LOGGING_CONFIG);
                }
            } finally {
                config.close();
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        }
    }

    /**
     * Read the config file contents as an {@link InputStream}
     * 
     * @param configFile
     * @return an inputstream
     */
    private static InputStream readConfig(String configFile) {
        InputStream stream = Application.class.getClassLoader().getResourceAsStream(configFile); // From classpath
        boolean isNotUrl = true;

        if (null == stream) {
            try {
                stream = new FileInputStream(configFile); // From local file
            } catch (FileNotFoundException exception) {
                isNotUrl = false;

                try {
                    stream = new URL(configFile).openStream(); // From remote URL
                } catch (IOException ioException) {
                    // no op
                }
            }
        }

        if (isNotUrl) {
            System.setProperty(PROP_LOGGING_CONFIG, configFile);
        }

        return stream;
    }
}
