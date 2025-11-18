package io.littlehorse.connector.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.connector.task.ConnectorTask;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class KubernetesService {
    private static final String ALREADY_EXISTS_ERROR_CODE = "AlreadyExists";
    private static Logger log = LoggerFactory.getLogger(ConnectorTask.class);
    private final KubernetesClient client;
    private final String defaultNamespace;

    public KubernetesService(
            final KubernetesClient client,
            @ConfigProperty(name = ConnectorConfig.DEFAULT_NAMESPACE)
                    final String defaultNamespace) {
        this.client = client;
        this.defaultNamespace = defaultNamespace;
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

    public void apply(final String yaml) {
        apply(defaultNamespace, yaml);
    }

    public void apply(final String namespace, final String yaml) {
        try {
            final HasMetadata metadata =
                    client.resource(yaml).inNamespace(namespace).create();
            log.info(
                    "Resource '{}/{}' successfully created",
                    metadata.getKind(),
                    metadata.getMetadata().getName());
        } catch (final KubernetesClientException e) {
            if (Optional.ofNullable(e.getStatus())
                    .map(Status::getReason)
                    .orElseThrow(() -> e)
                    .equals(ALREADY_EXISTS_ERROR_CODE)) {
                final HasMetadata updatedResource =
                        client.resource(yaml).inNamespace(namespace).update();
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
