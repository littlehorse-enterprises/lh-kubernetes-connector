package io.littlehorse.connector.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = ConnectorConfig.PREFIX)
public interface ConnectorConfig {

    String PREFIX = "littlehorse.connector.kubernetes";

    String TASK_APPLY_NAME = PREFIX + ".task.apply.name";
    String TASK_APPLY_NAME_EXPRESSION = "${" + TASK_APPLY_NAME + "}";
    String TASK_APPLY_ENABLED = PREFIX + ".task.apply.enabled";
    String TASK_APPLY_ENABLED_EXPRESSION = "${" + TASK_APPLY_ENABLED + "}";

    @WithName("task.apply")
    TaskConfig taskApply();

    String TASK_SECRET_NAME = PREFIX + ".task.secret.name";
    String TASK_SECRET_NAME_EXPRESSION = "${" + TASK_SECRET_NAME + "}";
    String TASK_SECRET_ENABLED = PREFIX + ".task.secret.enabled";
    String TASK_SECRET_ENABLED_EXPRESSION = "${" + TASK_SECRET_ENABLED + "}";

    @WithName("task.secret")
    TaskConfig taskSecret();

    String TASK_STATUS_NAME = PREFIX + ".task.status.name";
    String TASK_STATUS_NAME_EXPRESSION = "${" + TASK_STATUS_NAME + "}";
    String TASK_STATUS_ENABLED = PREFIX + ".task.status.enabled";
    String TASK_STATUS_ENABLED_EXPRESSION = "${" + TASK_STATUS_ENABLED + "}";

    @WithName("task.status")
    TaskConfig taskStatus();

    interface TaskConfig {
        String name();

        boolean enabled();
    }
}
