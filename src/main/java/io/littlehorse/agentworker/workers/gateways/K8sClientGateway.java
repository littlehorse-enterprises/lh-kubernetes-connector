package io.littlehorse.agentworker.workers.gateways;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.agentworker.workers.LHResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class K8sClientGateway {
    private static final Logger logger = LoggerFactory.getLogger(K8sClientGateway.class);
    private final KubernetesClient k8sClient;

    public K8sClientGateway(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    public void createOrUpdateResource(
            String clusterName, LHResources crdName, String resourceName, String crdToBeApplied) {
        try {

            if (crdName.equals(LHResources.LH_CLUSTER)) {
                logger.info("Trying to create/update a new [{}] named [{}].", crdName.getDescription(), resourceName);
            } else {
                logger.info(
                        "Trying to create/update a new [{}] [{}] within Cluster: [{}]",
                        crdName.getDescription(),
                        resourceName,
                        clusterName);
            }

            this.k8sClient.resource(crdToBeApplied).update();
            logger.info("LHTenant updated successfully.");
        } catch (KubernetesClientException exn) {
            if (errorWithoutCode(exn) || !resourceNotFound(exn)) {
                logger.error("An error occurred while updating [{}] in k8s.", crdName, exn);
                throw exn;
            }
            logger.info("Trying to create the [{}].", crdName);
            try {
                this.k8sClient.resource(crdToBeApplied).create();
                logger.info("[{}]  created successfully.", crdName);
            } catch (KubernetesClientException e) {
                logger.error("Error while creating [{}].", crdName, e);
                throw e;
            }
        }
    }

    private static boolean errorWithoutCode(KubernetesClientException exn) {
        return exn.getCode() == -1;
    }

    private static boolean resourceNotFound(KubernetesClientException exn) {
        return exn.getStatus() != null && exn.getStatus().getCode().equals(HttpStatus.NOT_FOUND.getCode());
    }
}
