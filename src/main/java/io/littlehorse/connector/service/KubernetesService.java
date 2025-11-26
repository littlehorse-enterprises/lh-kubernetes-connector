package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.exception.NotFoundException;
import io.littlehorse.infrastructure.kubernetes.KubernetesUtils;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class KubernetesService {
    private static Logger log = LoggerFactory.getLogger(KubernetesService.class);
    private final KubernetesClient client;
    private final String defaultNamespace;

    public KubernetesService(
            final KubernetesClient client,
            @ConfigProperty(name = ConnectorConfig.DEFAULT_NAMESPACE)
                    final String defaultNamespace) {
        this.client = client;
        this.defaultNamespace = defaultNamespace;
    }

    private static void logSuccess(final HasMetadata resource) {
        log.info(
                "Resource '{}/{}' successfully updated in namespace '{}'",
                resource.getKind(),
                resource.getMetadata().getName(),
                resource.getMetadata().getNamespace());
    }

    /**
     * Get resource status
     * @param apiVersion Specifies which version of the Kubernetes API you are using to create or interact with an object
     * @param kind Type of resource
     * @param namespace Specific namespace
     * @param name Name of the resource
     * @return Status
     */
    public Object status(
            final String apiVersion, final String kind, final String namespace, final String name) {
        final GenericKubernetesResource resource = client.genericKubernetesResources(
                        apiVersion, kind)
                .inNamespace(Optional.ofNullable(namespace).orElse(defaultNamespace))
                .withName(name)
                .get();

        if (resource == null) {
            throw new NotFoundException("Resource not found");
        }

        return Optional.ofNullable(resource.getAdditionalProperties())
                .map(properties -> properties.get("status"))
                .orElse(null);
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
