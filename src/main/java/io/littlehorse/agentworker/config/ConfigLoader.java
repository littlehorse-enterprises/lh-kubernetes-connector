package io.littlehorse.agentworker.config;

import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ConfigLoader {
    private ConfigLoader() {}

    /**
     * Loads properties in this order:
     * 1. application.properties or application.yml
     * 2. Environment variables
     * 3. System properties
     */
    public static final SmallRyeConfig CONFIG_ENGINE = new SmallRyeConfigBuilder()
            .addDefaultInterceptors()
            .addDefaultSources()
            .addDiscoveredSources()
            .addDiscoveredInterceptors()
            .withMapping(AgentWorkerConfig.class)
            .build();

    public static AgentWorkerConfig getConfig() {
        return CONFIG_ENGINE.getConfigMapping(AgentWorkerConfig.class);
    }
}
