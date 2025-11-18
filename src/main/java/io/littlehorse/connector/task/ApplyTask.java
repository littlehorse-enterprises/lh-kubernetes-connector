package io.littlehorse.connector.task;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;
import io.quarkus.arc.properties.IfBuildProperty;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
@IfBuildProperty(name = ConnectorConfig.TASK_APPLY_ENABLED, stringValue = "true")
public class ApplyTask {
    private static Logger log = LoggerFactory.getLogger(ApplyTask.class);
    private final String connectorTaskName;
    private final KubernetesService service;

    public ApplyTask(
            @ConfigProperty(name = ConnectorConfig.TASK_APPLY_NAME) final String connectorTaskName,
            final KubernetesService service) {
        this.connectorTaskName = connectorTaskName;
        this.service = service;
    }

    // TODO: define retriable and not retryable exceptions
    @LHTaskMethod(ConnectorConfig.TASK_APPLY_NAME_EXPRESSION)
    public void applyManifest(final String yaml, final WorkerContext context) {
        log.info(
                "Executing task '{}' with idempotency key '{}'",
                connectorTaskName,
                context.getIdempotencyKey());
        service.apply(yaml);
    }
}
