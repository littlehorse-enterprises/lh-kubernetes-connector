package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.task.ConnectorTask;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class KubernetesService {
    private static final String ALREADY_EXISTS_ERROR_CODE = "AlreadyExists";
    private static Logger log = LoggerFactory.getLogger(ConnectorTask.class);
    private final KubernetesClient client;

    public KubernetesService(final KubernetesClient client) {
        this.client = client;
    }

    public void apply(final String yaml) {
        try {
            final HasMetadata metadata = client.resource(yaml).create();
            log.info(
                    "Resource '{}/{}' successfully created",
                    metadata.getKind(),
                    metadata.getMetadata().getName());
        } catch (final KubernetesClientException e) {
            if (Optional.ofNullable(e.getStatus())
                    .map(Status::getReason)
                    .orElseThrow(() -> e)
                    .equals(ALREADY_EXISTS_ERROR_CODE)) {
                final HasMetadata updatedResource = client.resource(yaml).update();
                log.info(
                        "Resource '{}/{}' successfully updated",
                        updatedResource.getKind(),
                        updatedResource.getMetadata().getName());
                return;
            }
            throw e;
        }
    }
}
