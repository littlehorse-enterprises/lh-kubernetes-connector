package io.littlehorse.agentworker.workers;

import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateClusterTask {
    public static final String CREATE_LH_CLUSTER_IN_DP_DATA_PLANE_ID = "create-lh-cluster-in-dp-${data-plane-id}";
    private final K8sClientGateway k8sClientGateway;

    public CreateClusterTask(K8sClientGateway k8sClientGateway) {
        this.k8sClientGateway = k8sClientGateway;
    }

    @LHTaskMethod(CREATE_LH_CLUSTER_IN_DP_DATA_PLANE_ID)
    public ClusterHealthInfo createLHCluster(
            String clusterName, int horsepower, String dataPlaneId, String lhClusterResourcesYml) {

        log.info(
                "Trying to create Cluster with name {} with {} HP into DataPlane: {}",
                clusterName,
                horsepower,
                dataPlaneId);

        List<String> allResourcesToApply = Arrays.stream(lhClusterResourcesYml.split("---"))
                .filter(r -> !r.isEmpty())
                .toList();

        List<ClusterHealthInfo> healthForAllClusterRelatedResources = new ArrayList<>();

        for (String resourceYML : allResourcesToApply) {
            healthForAllClusterRelatedResources.add(k8sClientGateway.createOrUpdateResource(
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
