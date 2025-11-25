package io.littlehorse.connector.task;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.lookup.LookupIfProperty;

import org.apache.commons.lang3.StringUtils;

@LHTask
@LookupIfProperty(name = ConnectorConfig.TASK_STATUS_ENABLED, stringValue = "true")
public class StatusTask {
    private final KubernetesService service;

    public StatusTask(final KubernetesService service) {
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_STATUS_NAME_EXPRESSION)
    public Object status(
            final String apiVersion, final String kind, final String namespace, final String name) {

        if (StringUtils.isBlank(apiVersion)) {
            throw new BadRequestException("Resource apiVersion must not be blank");
        }

        if (StringUtils.isBlank(kind)) {
            throw new BadRequestException("Resource kind must not be blank");
        }

        if (StringUtils.isBlank(name)) {
            throw new BadRequestException("Resource name must not be blank");
        }

        try {
            return service.status(apiVersion, kind, namespace, name);
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new BadRequestException(e);
            }
            throw e;
        }
    }
}
