package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHClustersWorker {
    private static final Logger logger = LoggerFactory.getLogger(SecretsWorker.class);

    private final KubernetesClient k8sClient;

    public LHClustersWorker(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
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

        for (String resource : allResourcesToApply) {
            try {
                this.k8sClient.resource(resource).update();
                logger.info("Cluster with name {} updated successfully", clusterName);
            } catch (KubernetesClientException exn) {
                if (errorWithoutCode(exn) || !resourceNotFound(exn)) {
                    logger.error("An error occurred while creating a resource in k8s.", exn);
                    return new ClusterHealthInfo(ClusterStatus.UNHEALTHY, exn.getMessage());
                }

                logger.info("Trying to create cluster: {} in fake-org with {} horse power.", clusterName, horsepower);
                try {
                    this.k8sClient.resource(resource).create();
                    logger.info("Cluster with name {} created successfully", clusterName);
                } catch (KubernetesClientException e) {
                    logger.error("Error while creating LHCluster with name: {}", clusterName);
                    return new ClusterHealthInfo(ClusterStatus.UNHEALTHY, exn.getMessage());
                }
            }
        }

        return new ClusterHealthInfo(ClusterStatus.RUNNING, horsepower);
    }

    private static boolean errorWithoutCode(KubernetesClientException exn) {
        return exn.getCode() == -1;
    }

    private static boolean resourceNotFound(KubernetesClientException exn) {
        return exn.getStatus() != null && exn.getStatus().getCode().equals(HttpStatus.NOT_FOUND.getCode());
    }
}
