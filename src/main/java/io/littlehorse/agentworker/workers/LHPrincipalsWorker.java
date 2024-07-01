package io.littlehorse.agentworker.workers;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class LHPrincipalsWorker {
    private final K8sClientGateway k8sClientGateway;

    public LHPrincipalsWorker(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod("create-human-principal-in-dp-${data-plane-id}")
    public void createHumanPrincipalInDP(String lhPrincipalYML, String clusterName, String email) {
        this.k8sClientGateway.createOrUpdateResource(clusterName, "LHPrincipal", email, lhPrincipalYML);
    }
}
