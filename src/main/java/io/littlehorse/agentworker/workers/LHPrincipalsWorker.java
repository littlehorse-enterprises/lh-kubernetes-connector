package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.sdk.worker.LHTaskMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHPrincipalsWorker {
    private static final Logger logger = LoggerFactory.getLogger(SecretsWorker.class);
    private final KubernetesClient k8sClient;

    public LHPrincipalsWorker(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @LHTaskMethod("create-human-principal-in-dp-${data-plane-id}")
    public void createHumanPrincipalInDP(String lhPrincipalYML, String clusterName, String email) {
        logger.info("Trying to create/update a new LHPrincipal [{}] for Cluster: [{}]", email, clusterName);

        try {
            this.k8sClient.resource(lhPrincipalYML).update();
            logger.info("LHPrincipal updated successfully.");
        } catch (KubernetesClientException exn) {
            if (errorWithoutCode(exn) || !resourceNotFound(exn)) {
                logger.error("An error occurred while updating LHPrincipal in k8s.", exn);
                throw exn;
            }
            logger.info("Trying to create the LHPrincipal.");
            try {
                this.k8sClient.resource(lhPrincipalYML).create();
                logger.info("LHPrincipal created successfully.");
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
