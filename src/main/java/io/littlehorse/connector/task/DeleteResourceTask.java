package io.littlehorse.connector.task;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.exception.ForbiddenException;
import io.littlehorse.connector.kubernetes.KubernetesUtils;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.lookup.LookupIfProperty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
@LookupIfProperty(name = ConnectorConfig.TASK_DELETE_ENABLED, stringValue = "true")
public class DeleteResourceTask {
    private static Logger log = LoggerFactory.getLogger(DeleteResourceTask.class);
    private final KubernetesService service;

    public DeleteResourceTask(final KubernetesService service) {
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_DELETE_NAME_EXPRESSION)
    public void delete(
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
            service.delete(apiVersion, kind, namespace, name);

            log.debug(
                    "Resource apiVersion: {}, kind: {}, namespace: {}, name: {} successfully deleted",
                    apiVersion,
                    kind,
                    namespace,
                    name);
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isBadRequestException(e)) {
                throw new BadRequestException(e);
            } else if (KubernetesUtils.isForbiddenException(e)) {
                throw new ForbiddenException(e);
            }
            throw e;
        }
    }
}
