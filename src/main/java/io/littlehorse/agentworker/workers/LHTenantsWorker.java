package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class LHTenantsWorker {
    private final K8sClientGateway k8sClientGateway;

    public LHTenantsWorker(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod("create-lh-tenant-in-dp-${data-plane-id}")
    public void createLhTenant(String clusterName, String tenantName, String lhTenantYML) {
        this.k8sClientGateway.createOrUpdateResource(clusterName, LHResources.LH_TENANT, tenantName, lhTenantYML);
    }
}
