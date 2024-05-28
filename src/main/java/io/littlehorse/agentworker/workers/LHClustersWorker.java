package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javalin.http.HttpStatus;
import io.littlehorse.operator.cluster.LHCluster;
import io.littlehorse.operator.cluster.spec.LHClusterSpec;
import io.littlehorse.operator.cluster.spec.kafka.KafkaAccessSpec;
import io.littlehorse.operator.cluster.spec.kafka.strimzi.StrimziClusterRef;
import io.littlehorse.operator.cluster.spec.kafka.strimzi.StrimziListenerSpec;
import io.littlehorse.operator.cluster.spec.server.ImagePullPolicy;
import io.littlehorse.operator.cluster.spec.server.ServerSpec;
import io.littlehorse.operator.cluster.spec.server.ServerStorageSpec;
import io.littlehorse.operator.cluster.spec.server.listener.ListenerSpec;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LHClustersWorker {
    private static final Logger logger = LoggerFactory.getLogger(LHClustersWorker.class);
    private final KubernetesClient k8sClient;
    private final HorsePowerCalculator horsePowerCalculator;

    public LHClustersWorker(KubernetesClient k8sClient, HorsePowerCalculator horsePowerCalculator) {
        this.k8sClient = k8sClient;
        this.horsePowerCalculator = horsePowerCalculator;
    }

    @LHTaskMethod("create-cluster-in-fake-org")
    public void calculateAndDeployLHCluster(String clusterName, int horsepower)
            throws InvalidHorsePowerQuantityException {
        HorsePower computedSpecs = this.horsePowerCalculator.compute(horsepower);

        logger.info("Creating Cluster with name {} into DataPlane: fake-org", clusterName);

        ListenerSpec listener = new ListenerSpec();
        listener.setPort(2023);
        listener.setName("my-listener");

        ServerStorageSpec storage = new ServerStorageSpec();
        storage.setStorageClassName("standard");
        storage.setVolumeSize(computedSpecs.getVolumeSize());

        KafkaAccessSpec kafka = new KafkaAccessSpec();
        kafka.setReplicationFactor(computedSpecs.getReplicationFactor());
        kafka.setClusterPartitions(computedSpecs.getClusterPartitions());

        StrimziListenerSpec strimziListener = new StrimziListenerSpec();
        strimziListener.setAuthentication("TLS");
        strimziListener.setPort(9093);
        strimziListener.setTls(true);

        StrimziClusterRef strimzi = new StrimziClusterRef();
        strimzi.setCreateTopics(true);
        strimzi.setClusterName("lh-kafka");
        strimzi.setListener(strimziListener);
        kafka.setStrimziClusterRef(strimzi);

        ServerSpec server = new ServerSpec();
        server.setReplicas(computedSpecs.getReplicas());
        server.setImage("littlehorse/lh-server:latest");
        server.setImagePullPolicy(ImagePullPolicy.IfNotPresent);
        server.setListeners(List.of(listener));
        server.setStorage(storage);

        LHClusterSpec lhcluster = new LHClusterSpec();
        lhcluster.setServer(server);
        lhcluster.setKafka(kafka);

        LHCluster clusterToDeploy = new LHCluster();
        clusterToDeploy.setSpec(lhcluster);
        clusterToDeploy.setMetadata(
                new ObjectMetaBuilder().withName(clusterName).build());

        try {
            this.k8sClient.resource(clusterToDeploy).update();
        } catch (KubernetesClientException exn) {
            if (exn.getStatus().getCode().equals(HttpStatus.NOT_FOUND.getCode())) {
                this.k8sClient.resource(clusterToDeploy).create();
            } else {
                logger.error("Error while updating LHCluster with name: {}", clusterName);
                throw exn;
            }
        }
    }
}
