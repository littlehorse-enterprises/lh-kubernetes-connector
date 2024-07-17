package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class LHCRWorker {
    private final K8sClientGateway k8sClientGateway;

    public LHCRWorker(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod("create-resource-in-dp-${data-plane-id}")
    public void createResourceInDP(String resourceType, String lhPrincipalYML, String clusterName, String email) {
        try {
            LHResources lhResource = LHResources.valueOf(resourceType);
            this.k8sClientGateway.createOrUpdateResource(clusterName, lhResource, email, lhPrincipalYML);
        } catch (IllegalArgumentException e) {
            throw new LHTaskException("INVALID_RESOURCE_TYPE", "Invalid resource type: " + resourceType);
        }
    }
}
