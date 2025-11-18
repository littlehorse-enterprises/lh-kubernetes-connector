package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.littlehorse.connector.task.ApplyTask;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class KubernetesService {
    private static final String ALREADY_EXISTS_ERROR_CODE = "AlreadyExists";
    private static Logger log = LoggerFactory.getLogger(ApplyTask.class);
    private final KubernetesClient client;

    public KubernetesService(final KubernetesClient client) {
        this.client = client;
    }

    //
    //    public Boolean isReady(){
    //        return
    // client.genericKubernetesResources("","").inNamespace("").withName("").isReady();
    //    }
    //
    //    public Stream<PodStatus> getPodListStatus(){
    //        return client.pods().inNamespace("")
    //                .withLabelSelector("").list()
    //                .getItems().stream().map(Pod::getStatus);
    //    }
    //
    //    public PodStatus getPodStatus(){
    //        return client.pods().inNamespace("")
    //                .withName("")
    //                .get().getStatus();
    //    }
    /**
     * Save (create/update) a secret.
     *
     * @param secret Secret to be saved.
     */
    public void save(final Secret secret) {
        final Resource<Secret> resource = client.secrets().resource(secret);

        try {
            logSuccess(resource.create());
        } catch (final KubernetesClientException e) {
            if (isAlreadyExistsException(e)) {
                logSuccess(resource.update());
                return;
            }
            throw e;
        }
    }

    /**
     * Manifest to be applied.
     * If the manifest does not provide a namespace the service will use the default one.
     *
     * @param yaml Manifest yaml file.
     */
    public void apply(final String yaml) {
        final NamespaceableResource<HasMetadata> resource = client.resource(yaml);

        try {
            logSuccess(resource.create());
        } catch (final KubernetesClientException e) {
            if (isAlreadyExistsException(e)) {
                logSuccess(resource.update());
                return;
            }
            throw e;
        }
    }

    private static boolean isAlreadyExistsException(final KubernetesClientException e) {
        return Optional.ofNullable(e.getStatus())
                .map(Status::getReason)
                .map(reason -> reason.equals(ALREADY_EXISTS_ERROR_CODE))
                .orElse(false);
    }

    private static void logSuccess(final HasMetadata resource) {
        log.info(
                "Resource '{}/{}' successfully updated in namespace '{}'",
                resource.getKind(),
                resource.getMetadata().getName(),
                resource.getMetadata().getNamespace());
    }
}
