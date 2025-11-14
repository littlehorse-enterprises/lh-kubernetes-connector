package io.littlehorse.connector.task;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class ConnectorTask {
    private static Logger log = LoggerFactory.getLogger(ConnectorTask.class);
    private final String connectorTaskName;
    private final KubernetesService service;

    public ConnectorTask(
            @ConfigProperty(name = ConnectorConfig.TASK_NAME) final String connectorTaskName,
            final KubernetesService service) {
        this.connectorTaskName = connectorTaskName;
        this.service = service;
    }

    // TODO: define retriable and not retryable exceptions
    @LHTaskMethod(ConnectorConfig.TASK_NAME_EXPRESSION)
    public void applyManifest(final String yaml, final WorkerContext context) {
        log.info(
                "Executing task '{}' with idempotency key '{}'",
                connectorTaskName,
                context.getIdempotencyKey());
        service.apply(yaml);
    }
}
