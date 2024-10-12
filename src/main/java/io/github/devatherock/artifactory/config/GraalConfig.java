package io.github.devatherock.artifactory.config;

import io.micronaut.core.annotation.ReflectionConfig;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.core.annotation.TypeHint.AccessType;
import net.logstash.logback.encoder.LogstashEncoder;

@ReflectionConfig(type = LogstashEncoder.class, accessType = AccessType.ALL_DECLARED_CONSTRUCTORS)
@TypeHint(typeNames = {
        "com.github.benmanes.caffeine.cache.SSMSAW",
        "com.github.benmanes.caffeine.cache.PSAWMS",
}, accessType = {
        AccessType.ALL_DECLARED_CONSTRUCTORS,
        AccessType.ALL_DECLARED_METHODS,
})
public class GraalConfig {
}
