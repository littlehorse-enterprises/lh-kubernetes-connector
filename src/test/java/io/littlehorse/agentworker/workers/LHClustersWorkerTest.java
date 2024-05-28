package io.littlehorse.agentworker.workers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.littlehorse.operator.cluster.LHCluster;
import io.littlehorse.operator.cluster.spec.LHClusterSpec;
import io.littlehorse.operator.cluster.spec.kafka.KafkaAccessSpec;
import io.littlehorse.operator.cluster.spec.kafka.strimzi.StrimziClusterRef;
import io.littlehorse.operator.cluster.spec.kafka.strimzi.StrimziListenerSpec;
import io.littlehorse.operator.cluster.spec.server.ImagePullPolicy;
import io.littlehorse.operator.cluster.spec.server.ServerSpec;
import io.littlehorse.operator.cluster.spec.server.ServerStorageSpec;
import io.littlehorse.operator.cluster.spec.server.listener.ListenerSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class LHClustersWorkerTest {
    @Test
    void shouldAskK8sToDeployAClusterWithTheProvidedNameAndHorsePower() throws InvalidHorsePowerQuantityException {
        KubernetesClient kubernetesClientMock = MockKubernetesClient.client(HasMetadata.class);
        LHClustersWorker lhClustersWorker = new LHClustersWorker(kubernetesClientMock, new HorsePowerCalculator());

        ListenerSpec listener = new ListenerSpec();
        listener.setPort(2023);
        listener.setName("my-listener");

        ServerStorageSpec storage = new ServerStorageSpec();
        storage.setStorageClassName("standard");
        storage.setVolumeSize(new Quantity("10G"));

        KafkaAccessSpec kafka = new KafkaAccessSpec();
        kafka.setReplicationFactor(3);
        kafka.setClusterPartitions(6);

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
        server.setReplicas(9);
        server.setImage("littlehorse/lh-server:latest");
        server.setImagePullPolicy(ImagePullPolicy.IfNotPresent);
        server.setListeners(List.of(listener));
        server.setStorage(storage);

        LHClusterSpec lhcluster = new LHClusterSpec();
        lhcluster.setServer(server);
        lhcluster.setKafka(kafka);

        LHCluster expectedClusterToDeploy = new LHCluster();
        expectedClusterToDeploy.setSpec(lhcluster);
        expectedClusterToDeploy.setMetadata(
                new ObjectMetaBuilder().withName("ANY_CLUSTER").build());

        NamespaceableResource<LHCluster> mockResource = mock();
        when(kubernetesClientMock.resource(expectedClusterToDeploy)).thenReturn(mockResource);

        lhClustersWorker.calculateAndDeployLHCluster("ANY_CLUSTER", 3);

        verify(kubernetesClientMock, times(1)).resource(expectedClusterToDeploy);
        verify(mockResource, times(1)).update();
        verify(mockResource, times(0)).create();
    }

    @Test
    void IfTheClusterIsNotFoundWeShouldCreateANewOneInsteadOfUpdatingIt() throws InvalidHorsePowerQuantityException {
        KubernetesClient kubernetesClientMock = MockKubernetesClient.client(HasMetadata.class);
        LHClustersWorker lhClustersWorker = new LHClustersWorker(kubernetesClientMock, new HorsePowerCalculator());

        LHCluster expectedClusterToDeploy = mock();

        NamespaceableResource<LHCluster> mockResource = mock();
        when(kubernetesClientMock.resource(any(LHCluster.class))).thenReturn(mockResource);
        when(mockResource.update())
                .thenThrow(new KubernetesClientException(
                        "Cluster not found.",
                        404,
                        new Status("v1", 404, mock(), "Status", "Not Found", mock(), "notFound", "failure")));

        lhClustersWorker.calculateAndDeployLHCluster("ANY_CLUSTER", 3);

        verify(kubernetesClientMock, times(2)).resource(any(LHCluster.class));
        verify(mockResource, times(1)).update();
        verify(mockResource, times(1)).create();
    }

    @Test
    void IfUpdatingTheClusterReturnsAnyOtherErrorWeShouldThrowIt() throws InvalidHorsePowerQuantityException {
        KubernetesClient kubernetesClientMock = MockKubernetesClient.client(HasMetadata.class);
        LHClustersWorker lhClustersWorker = new LHClustersWorker(kubernetesClientMock, new HorsePowerCalculator());

        LHCluster expectedClusterToDeploy = mock();

        NamespaceableResource<LHCluster> mockResource = mock();
        when(kubernetesClientMock.resource(any(LHCluster.class))).thenReturn(mockResource);
        when(mockResource.update())
                .thenThrow(new KubernetesClientException(
                        "Cluster not found.",
                        500,
                        new Status("v1", 500, mock(), "Status", "Internal error", mock(), "InternalError", "failure")));

        assertThatThrownBy(() -> {
                    lhClustersWorker.calculateAndDeployLHCluster("ANY_CLUSTER", 3);
                    verify(kubernetesClientMock, times(1)).resource(any(LHCluster.class));
                    verify(mockResource, times(1)).update();
                    verify(mockResource, times(0)).create();
                })
                .isInstanceOf(KubernetesClientException.class);
    }
}
