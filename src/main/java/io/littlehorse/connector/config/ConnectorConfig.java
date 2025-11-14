package io.littlehorse.connector.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = ConnectorConfig.PREFIX)
public interface ConnectorConfig {

    String PREFIX = "littlehorse.connector.kubernetes";
    String TASK_NAME = PREFIX + ".task.name";
    String TASK_NAME_EXPRESSION = "${" + TASK_NAME + "}";

    TaskConfig task();

    interface TaskConfig {
        String name();
    }
}
