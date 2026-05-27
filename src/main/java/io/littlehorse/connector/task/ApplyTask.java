package io.littlehorse.connector.task;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.exception.ForbiddenException;
import io.littlehorse.connector.service.KubernetesService;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.quarkus.arc.lookup.LookupIfProperty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
@LookupIfProperty(name = ConnectorConfig.TASK_APPLY_ENABLED, stringValue = "true")
public class ApplyTask {
    private static Logger log = LoggerFactory.getLogger(ApplyTask.class);
    private final KubernetesService service;

    public ApplyTask(final KubernetesService service) {
        this.service = service;
    }

    @LHTaskMethod(ConnectorConfig.TASK_APPLY_NAME_EXPRESSION)
    public void apply(final String yaml) {
        if (StringUtils.isBlank(yaml)) {
            throw new BadRequestException("Yaml must not be blank");
        }

        try {
            final HasMetadata resource = service.apply(yaml);
            log.debug(
                    "Resource '{}/{}' successfully updated in namespace '{}'",
                    resource.getKind(),
                    resource.getMetadata().getName(),
                    resource.getMetadata().getNamespace());
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
