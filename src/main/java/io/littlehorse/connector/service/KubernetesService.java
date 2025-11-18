package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.littlehorse.connector.task.ApplyTask;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KubernetesService {
    private static Logger log = LoggerFactory.getLogger(ApplyTask.class);
    private final KubernetesClient client;

    public KubernetesService(final KubernetesClient client) {
        this.client = client;
    }

    private static void logSuccess(final HasMetadata resource) {
        log.info(
                "Resource '{}/{}' successfully updated in namespace '{}'",
                resource.getKind(),
                resource.getMetadata().getName(),
                resource.getMetadata().getNamespace());
    }

    /**
     * Save (create/update) a secret.
     *
     * @param secret Secret to be saved.
     */
    public void save(final Secret secret) {
        apply(client.secrets().resource(secret));
    }

    /**
     * Manifest to be applied.
     * If the manifest does not provide a namespace the service will use the default one.
     *
     * @param yaml Manifest yaml file.
     */
    public void apply(final String yaml) {
        apply(client.resource(yaml));
    }

    /**
     * Resource to be applied.
     *
     * @param resource Any resource.
     */
    public void apply(final Resource<? extends HasMetadata> resource) {
        try {
            logSuccess(resource.create());
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isAlreadyExistsException(e)) {
                logSuccess(resource.update());
                return;
            }
            throw e;
        }
    }
}
