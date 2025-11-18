package io.littlehorse.connector.task;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.quarkus.arc.properties.IfBuildProperty;

import java.util.Map;

@LHTask
@IfBuildProperty(name = ConnectorConfig.TASK_SECRET_ENABLED, stringValue = "true")
public class SecretTask {

    private final KubernetesService service;

    public SecretTask(final KubernetesService service) {
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_SECRET_NAME_EXPRESSION)
    public void save(
            final String namespace,
            final String name,
            final Map<String, String> labels,
            @LHType(masked = true) final Map<String, String> stringData,
            @LHType(masked = true) final Map<String, String> data) {

        final Secret secret = new SecretBuilder()
                .editMetadata()
                .withName(name)
                .withNamespace(namespace)
                .withLabels(labels)
                .endMetadata()
                .withStringData(stringData)
                .withData(data)
                .build();

        try {
            service.save(secret);
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new LHTaskException("bad-request", e.getMessage());
            }
            throw e;
        }
    }
}
