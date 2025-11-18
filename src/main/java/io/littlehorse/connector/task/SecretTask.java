package io.littlehorse.connector.task;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.littlehorse.sdk.worker.WorkerContext;
import io.quarkus.arc.properties.IfBuildProperty;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@LHTask
@IfBuildProperty(name = ConnectorConfig.TASK_SECRET_ENABLED, stringValue = "true")
public class SecretTask {
    private static Logger log = LoggerFactory.getLogger(ApplyTask.class);
    private final String taskName;
    private final KubernetesService service;

    public SecretTask(
            @ConfigProperty(name = ConnectorConfig.TASK_SECRET_NAME) final String taskName,
            final KubernetesService service) {
        this.taskName = taskName;
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_SECRET_NAME_EXPRESSION)
    public void save(
            final String namespace,
            final String name,
            final Map<String, String> labels,
            @LHType(masked = true) final Map<String, String> stringData,
            @LHType(masked = true) final Map<String, String> data,
            final WorkerContext context) {
        log.info(
                "Executing task '{}' with idempotency key '{}'",
                taskName,
                context.getIdempotencyKey());
        final Secret secret = new SecretBuilder()
                .editMetadata()
                .withName(name)
                .withNamespace(namespace)
                .withLabels(labels)
                .endMetadata()
                .withStringData(stringData)
                .withData(data)
                .build();
        service.save(secret);
    }
}
