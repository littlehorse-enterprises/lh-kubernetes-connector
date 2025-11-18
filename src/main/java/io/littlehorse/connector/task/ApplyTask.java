package io.littlehorse.connector.task;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;
import io.quarkus.arc.properties.IfBuildProperty;

@LHTask
@IfBuildProperty(name = ConnectorConfig.TASK_APPLY_ENABLED, stringValue = "true")
public class ApplyTask {
    private final KubernetesService service;

    public ApplyTask(final KubernetesService service) {
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_APPLY_NAME_EXPRESSION)
    public void apply(final String yaml, final WorkerContext context) {
        try {
            service.apply(yaml);
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new BadRequestException(e);
            }
            throw e;
        }
    }
}
