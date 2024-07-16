package io.littlehorse.agentworker.workers.gateways;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.agentworker.workers.ClusterHealthInfo;
import io.littlehorse.agentworker.workers.ClusterStatus;
import io.littlehorse.agentworker.workers.LHResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class K8sClientGateway {
    private static final Logger logger = LoggerFactory.getLogger(K8sClientGateway.class);
    private final KubernetesClient k8sClient;

    public K8sClientGateway(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    public ClusterHealthInfo createOrUpdateResource(
            String clusterName, LHResources crdName, String resourceName, String crdToBeApplied) {

        try {

            if (crdName.equals(LHResources.LH_CLUSTER)) {
                logger.info("Trying to update a new [{}] named [{}].", crdName.getDescription(), resourceName);
            } else {
                logger.info(
                        "Trying to update a new [{}] [{}] within Cluster: [{}]",
                        crdName.getDescription(),
                        resourceName,
                        clusterName);
            }

            this.k8sClient.resource(crdToBeApplied).update();

            logger.info("{} updated successfully.", resourceName);
            return new ClusterHealthInfo(ClusterStatus.RUNNING);
        } catch (KubernetesClientException exn) {
            if (resourceNotFound(exn)) {
                logger.info(
                        "Resource does not exist yet. Trying to create the [{}] named [{}].", crdName, resourceName);
                try {
                    this.k8sClient.resource(crdToBeApplied).create();
                    logger.info("[{}]  created successfully.", crdName);
                    return new ClusterHealthInfo(ClusterStatus.RUNNING);
                } catch (KubernetesClientException e) {
                    if (isBadRequest(exn)) {
                        return new ClusterHealthInfo(
                                ClusterStatus.UNHEALTHY, "Invalid request to create the resource:" + crdName);
                    }

                    logger.error("Error while creating [{}].", crdName, e);
                    throw e;
                }
            } else if (isBadRequest(exn)) {
                return new ClusterHealthInfo(
                        ClusterStatus.UNHEALTHY, "Invalid request to create the resource:" + crdName);
            } else {
                logger.error("An error occurred while updating [{}] in k8s.", crdName, exn);
                throw exn;
            }
        }
    }

    private static boolean isBadRequest(KubernetesClientException exn) {
        return exn.getStatus() != null && exn.getStatus().getCode().equals(HttpStatus.BAD_REQUEST.getCode());
    }

    private static boolean errorWithoutCode(KubernetesClientException exn) {
        return exn.getCode() == -1;
    }

    private static boolean resourceNotFound(KubernetesClientException exn) {
        return exn.getStatus() != null && exn.getStatus().getCode().equals(HttpStatus.NOT_FOUND.getCode());
    }
}
