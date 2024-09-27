package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplyYamlTask {
    public static final String CREATE_OR_UPDATE_RESOURCE = "create-or-update-resource-${k8s-cluster-id}";
    private final KubernetesClient kubernetesClient;
    private static final String K8S_ALREADY_EXISTS_ERROR_CODE = "AlreadyExists";

    public ApplyYamlTask(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @LHTaskMethod(CREATE_OR_UPDATE_RESOURCE)
    public void createOrUpdateResource(String resourceYaml) {
        try {
            HasMetadata createdResource = kubernetesClient.resource(resourceYaml).create();
            log.info("Resource {} successfully created.", createdResource.getMetadata().getName());
        } catch (KubernetesClientException e) {
            log.warn("K8s Exception caught: {}", e.getMessage());

            if (e.getStatus().getReason().equalsIgnoreCase(K8S_ALREADY_EXISTS_ERROR_CODE)) {
                HasMetadata updatedResource = kubernetesClient.resource(resourceYaml).update();

                log.info("Resource {} successfully updated.", updatedResource.getMetadata().getName());
            } else {
                throw new LHTaskException("K8s Exception", e.getMessage());
            }
        } catch (Exception e) {
            throw new LHTaskException("Unknown Exception in Agent", e.getMessage());
        }
    }
}
