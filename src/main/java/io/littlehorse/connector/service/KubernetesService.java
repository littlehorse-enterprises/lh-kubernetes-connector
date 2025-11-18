package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
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

    //    public void createSecret(Secret secret){
    //        // TODO: labels?
    //        // TODO: masked
    //        client.secrets().inNamespace("").resource(secret).create();
    //  }
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
     * Manifest to be applied.
     * If the manifest does not provide a namespace the service will use the default one.
     *
     * @param yaml Manifest yaml file.
     */
    public void apply(final String yaml) {
        final NamespaceableResource<HasMetadata> resource = client.resource(yaml);

        try {
            final HasMetadata createdResource = resource.create();
            log.info(
                    "Resource '{}/{}' successfully created in namespace '{}'",
                    createdResource.getKind(),
                    createdResource.getMetadata().getName(),
                    createdResource.getMetadata().getNamespace());
        } catch (final KubernetesClientException e) {
            if (isAlreadyExistsException(e)) {
                final HasMetadata updatedResource = resource.update();
                log.info(
                        "Resource '{}/{}' successfully updated namespace '{}'",
                        updatedResource.getKind(),
                        updatedResource.getMetadata().getName(),
                        updatedResource.getMetadata().getNamespace());
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
}
