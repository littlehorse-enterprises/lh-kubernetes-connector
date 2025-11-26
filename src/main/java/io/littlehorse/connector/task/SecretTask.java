package io.littlehorse.connector.task;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import io.quarkus.arc.lookup.LookupIfProperty;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@LHTask
@LookupIfProperty(name = ConnectorConfig.TASK_SECRET_ENABLED, stringValue = "true")
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

        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Secret name must not be blank");
        }

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
            service.apply(secret);
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new BadRequestException(e);
            }
            throw e;
        }
    }
}
