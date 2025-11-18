package io.littlehorse.connector.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = ConnectorConfig.PREFIX)
public interface ConnectorConfig {

    String PREFIX = "littlehorse.connector.kubernetes";

    String DEFAULT_NAMESPACE = PREFIX + ".default.namespace";
    String DEFAULT_NAMESPACE_EXPRESSION = "${" + DEFAULT_NAMESPACE + "}";

    @WithName("default.namespace")
    String defaultNamespace();

    String TASK_APPLY_NAME = PREFIX + ".task.apply.name";
    String TASK_APPLY_NAME_EXPRESSION = "${" + TASK_APPLY_NAME + "}";
    String TASK_APPLY_ENABLED = PREFIX + ".task.apply.enabled";
    String TASK_APPLY_ENABLED_EXPRESSION = "${" + TASK_APPLY_ENABLED + "}";

    @WithName("task.apply")
    TaskConfig taskApply();

    interface TaskConfig {
        String name();

        boolean enabled();
    }
}
