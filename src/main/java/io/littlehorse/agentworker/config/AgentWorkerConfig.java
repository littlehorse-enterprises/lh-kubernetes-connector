package io.littlehorse.agentworker.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "aw")
public interface AgentWorkerConfig {

    String clusterId();

    @WithDefault("8091")
    int restPort();
}
