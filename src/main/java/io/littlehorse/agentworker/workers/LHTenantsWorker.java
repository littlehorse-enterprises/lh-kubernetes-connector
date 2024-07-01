package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.agentworker.Main;
import io.littlehorse.sdk.worker.LHTaskMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHTenantsWorker {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final KubernetesClient k8sClient;

    public LHTenantsWorker(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @LHTaskMethod("create-lh-tenant-in-dp-${data-plane-id}")
    public void createLhTenant( String clusterName,
                                String tenantName,
                                String lhTenantYML) {

        try {
            logger.info("Trying to create/update a new LHTenant [{}] within Cluster: [{}]", tenantName, clusterName);

            this.k8sClient.resource(lhTenantYML).update();
            logger.info("LHTenant updated successfully.");
        } catch (KubernetesClientException exn) {
            if (errorWithoutCode(exn) || !resourceNotFound(exn)) {
                logger.error("An error occurred while updating LHPrincipal in k8s.", exn);
                throw exn;
            }
            logger.info("Trying to create the LHPrincipal.");
            try {
                this.k8sClient.resource(lhTenantYML).create();
                logger.info("LHPrincipal  created successfully.");
            } catch (KubernetesClientException e) {
                logger.error("Error while creating LHPrincipal.", e);
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
