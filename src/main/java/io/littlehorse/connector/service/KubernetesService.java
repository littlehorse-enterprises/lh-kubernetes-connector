package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.littlehorse.connector.kubernetes.KubernetesUtils;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class KubernetesService {
    private final KubernetesClient client;

    public KubernetesService(final KubernetesClient client) {
        this.client = client;
    }

    /**
     * Get resource.
     *
     * @param apiVersion Specifies which version of the Kubernetes API you are using to create or interact with an object.
     * @param kind Type of resource.
     * @param namespace Specific namespace.
     * @param name Name of the resource.
     * @return Optional resource.
     */
    public Optional<GenericKubernetesResource> get(
            final String apiVersion, final String kind, final String namespace, final String name) {

        final Resource<GenericKubernetesResource> resource = Objects.requireNonNull(
                Optional.ofNullable(namespace)
                        .map(nullableNamespace -> client.genericKubernetesResources(
                                        apiVersion, kind)
                                .inNamespace(nullableNamespace)
                                .withName(name))
                        .orElse(client.genericKubernetesResources(apiVersion, kind)
                                .withName(name)),
                "Provide a valid resource operation");

        return Optional.ofNullable(resource.get());
    }

    /**
     * Save (create/update) a secret.
     *
     * @param secret Secret to be saved.
     * @return Result.
     */
    public HasMetadata apply(final Secret secret) {
        return apply(client.secrets().resource(secret));
    }

    /**
     * Delete a resource.
     *
     * @param apiVersion Specifies which version of the Kubernetes API you are using to create or interact with an
     *     object.
     * @param kind Type of resource.
     * @param namespace Specific namespace.
     * @param name Name of the resource.
     */
    public void delete(
            final String apiVersion, final String kind, final String namespace, final String name) {
        delete(Optional.ofNullable(namespace)
                .map(nullableNamespace -> client.genericKubernetesResources(apiVersion, kind)
                        .inNamespace(nullableNamespace)
                        .withName(name))
                .orElse(client.genericKubernetesResources(apiVersion, kind).withName(name)));
    }

    /**
     * Manifest to be applied.
     * If the manifest does not provide a namespace the service will use the default one.
     *
     * @param yaml Manifest yaml file.
     * @return Result.
     */
    public HasMetadata apply(final String yaml) {
        return apply(client.resource(yaml));
    }

    /**
     * Resource to be applied.
     *
     * @param resource Any resource.
     * @return Result.
     */
    private HasMetadata apply(final Resource<? extends HasMetadata> resource) {
        final Resource<? extends HasMetadata> requiredResource =
                Objects.requireNonNull(resource, "Provide a valid resource operation");

        try {
            return requiredResource.create();
        } catch (final KubernetesClientException e) {
            if (KubernetesUtils.isAlreadyExistsException(e)) {
                return requiredResource.update();
            }
            throw e;
        }
    }

    /**
     * Resource to be deleted.
     *
     * @param resource Any resource.
     */
    private void delete(final Resource<? extends HasMetadata> resource) {
        if (resource != null) resource.delete();
    }
}
