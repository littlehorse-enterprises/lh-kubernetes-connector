package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class ApplyYamlTask {
    public static final String CREATE_RESOURCE_IN_DP_DATA_PLANE_ID = "create-resource-in-dp-${data-plane-id}";
    private final K8sClientGateway k8sClientGateway;

    public ApplyYamlTask(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod(CREATE_RESOURCE_IN_DP_DATA_PLANE_ID)
    public void createResourceInDP(String resourceType, String resourceYML, String clusterName, String resourceName) {
        try {
            LHResources lhResource = LHResources.valueOf(resourceType);
            k8sClientGateway.createOrUpdateResource(clusterName, lhResource, resourceName, resourceYML);
        } catch (IllegalArgumentException e) {
            throw new LHTaskException("INVALID_RESOURCE_TYPE", "Invalid resource type: " + resourceType);
        }
    }
}
