package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHClustersWorker {
    private static final Logger logger = LoggerFactory.getLogger(LHClustersWorker.class);
    private final K8sClientGateway k8sClientGateway;
    private final LittleHorseGrpc.LittleHorseBlockingStub lhClient;

    public LHClustersWorker(K8sClientGateway k8sClientGateway, LittleHorseGrpc.LittleHorseBlockingStub lhClient) {
        this.k8sClientGateway = k8sClientGateway;
        this.lhClient = lhClient;
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

        List<ClusterHealthInfo> healthForAllClusterRelatedResources = new ArrayList<>();

        for (String resourceYML : allResourcesToApply) {
            healthForAllClusterRelatedResources.add(this.k8sClientGateway.createOrUpdateResource(
                    clusterName, LHResources.LH_CLUSTER, clusterName, resourceYML));
        }

        List<ClusterHealthInfo> nonHealthyResources = healthForAllClusterRelatedResources.stream()
                .filter(s -> !s.getClusterStatus().equals(ClusterStatus.RUNNING))
                .toList();

        if (nonHealthyResources.isEmpty()) {
            return new ClusterHealthInfo(ClusterStatus.RUNNING);
        }
        return new ClusterHealthInfo(
                ClusterStatus.UNHEALTHY, "Some of the resources needed for the LH Cluster failed to be deployed.");
    }
}
