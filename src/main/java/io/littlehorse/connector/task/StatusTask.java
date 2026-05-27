package io.littlehorse.connector.task;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.exception.NotFoundException;
import io.littlehorse.connector.exception.UnavailableStatusException;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.lookup.LookupIfProperty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@LHTask
@LookupIfProperty(name = ConnectorConfig.TASK_STATUS_ENABLED, stringValue = "true")
public class StatusTask {
    private static Logger log = LoggerFactory.getLogger(StatusTask.class);
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
            final GenericKubernetesResource resource = service.get(
                            apiVersion, kind, namespace, name)
                    .orElseThrow(() -> new NotFoundException("Resource not found"));

            final Object status = Optional.ofNullable(resource.getAdditionalProperties())
                    .map(properties -> properties.get("status"))
                    .orElseThrow(() -> new UnavailableStatusException("Status unavailable"));

            log.debug(
                    "Status of resource apiVersion: {}, kind: {}, namespace: {}, name: {}, status: {}",
                    resource.getApiVersion(),
                    resource.getKind(),
                    resource.getMetadata().getNamespace(),
                    resource.getMetadata().getName(),
                    status);

            return status;
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new BadRequestException(e);
            }
            throw e;
        }
    }
}
