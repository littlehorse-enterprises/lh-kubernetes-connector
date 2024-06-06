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
        System.out.println("YML");
        System.out.println(clusterName);
        System.out.println(introspectionClientSecretYML);

        try {
            logger.info(
                    "Trying to create introspection secret for for LHCluster with name:: {} in Data Plane: aws-uw1-0.",
                    clusterName);
            this.k8sClient.resource(introspectionClientSecretYML).create();
        } catch (KubernetesClientException exn) {
            logger.error(
                    "Error while creating introspection secret for LHCluster with name: {} in Data Plane: aws-uw1-0",
                    clusterName);
            throw exn;
        }
    }
}
