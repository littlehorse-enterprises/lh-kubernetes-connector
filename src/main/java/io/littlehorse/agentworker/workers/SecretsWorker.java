package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.worker.LHTaskMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretsWorker {

    private static final Logger logger = LoggerFactory.getLogger(SecretsWorker.class);

    private final KubernetesClient k8sClient;

    public SecretsWorker(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @LHTaskMethod("create-introspection-secret-for-cluster-in-dp-aws-uw1-0")
    public void createIntrospectionSecret(String clusterName, String introspectionClientSecretYML) {
        String dataPlaneId = System.getenv().get("AW_DATA_PLANE_ID");
        try {
            logger.info(
                    "Trying to create introspection secret for for LHCluster with name:: {} in Data Plane: {}.",
                    clusterName,
                    dataPlaneId);
            this.k8sClient.resource(introspectionClientSecretYML).create();
        } catch (KubernetesClientException exn) {
            logger.error(
                    "Error while creating introspection secret for LHCluster with name: {} in Data Plane: {}",
                    clusterName,
                    dataPlaneId);
            throw exn;
        }
    }

    @LHTaskMethod("create-secret-for-lh-dashboard-in-dp-aws-uw1-0")
    public void createLHDashboardSecret(String clusterName, String dashboardClientSecretYML) {
        String dataPlaneId = System.getenv().get("AW_DATA_PLANE_ID");
        try {
            logger.info(
                    "Trying to create lh-dashboard secret with name:: {} in Data Plane: {}.", clusterName, dataPlaneId);
            this.k8sClient.resource(dashboardClientSecretYML).create();
        } catch (KubernetesClientException exn) {
            logger.error(
                    "Error while creating lh-dashboard secret for LHCluster with name: {} in Data Plane: {}",
                    clusterName,
                    dataPlaneId);
            throw exn;
        }
    }
}
