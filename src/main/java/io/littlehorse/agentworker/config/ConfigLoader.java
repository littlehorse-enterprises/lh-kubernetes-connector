package io.littlehorse.agentworker.config;

import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ConfigLoader {

    private final SmallRyeConfig config;

    /**
     * Loads properties in this order:
     * 1. application.properties or application.yml
     * 2. Environment variables
     * 3. System properties
     */
    public ConfigLoader() {
        config = new SmallRyeConfigBuilder()
                .addDefaultInterceptors()
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredInterceptors()
                .withMapping(AgentWorkerConfig.class)
                .build();
    }

    public AgentWorkerConfig getConfig() {
        return config.getConfigMapping(AgentWorkerConfig.class);
    }
}
