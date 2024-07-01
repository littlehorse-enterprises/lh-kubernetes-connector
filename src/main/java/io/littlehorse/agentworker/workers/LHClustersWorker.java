package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHClustersWorker {
    private static final Logger logger = LoggerFactory.getLogger(LHClustersWorker.class);
    private final K8sClientGateway k8sClientGateway;

    public LHClustersWorker(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod("create-lh-cluster-in-dp-${data-plane-id}")
    public ClusterHealthInfo createLHCluster(
            String clusterName, int horsepower, String dataPlaneId, String lhClusterResourcesYml) {

        logger.info(
                "Trying to create Cluster with name {} with {} HP into DataPlane: {}",
                clusterName,
                horsepower,
                dataPlaneId);

        List<String> allResourcesToApply = Arrays.stream(lhClusterResourcesYml.split("\\-\\-\\-"))
                .filter(r -> !r.isEmpty())
                .toList();

        for (String resourceYML : allResourcesToApply) {
            this.k8sClientGateway.createOrUpdateResource(clusterName, LHResources.LH_CLUSTER, clusterName, resourceYML);
        }

        return new ClusterHealthInfo(ClusterStatus.RUNNING, horsepower);
    }
}
