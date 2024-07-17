package io.littlehorse.agentworker.di;

import dagger.Module;
import dagger.Provides;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.HealthController;
import io.littlehorse.agentworker.workers.*;
import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import javax.inject.Singleton;

@Module
public class AgentWorkerModule {
    @Provides
    @Singleton
    public LHConfig provideLHConfig() {
        return new LHConfig();
    }

    @Provides
    @Singleton
    public LittleHorseGrpc.LittleHorseBlockingStub provideLittleHorseGrpcBlockingStub(LHConfig lhConfig) {
        return lhConfig.getBlockingStub();
    }

    @Provides
    @Singleton
    public HealthController provideHealthController() {
        return new HealthController();
    }

    @Provides
    @Singleton
    public KubernetesClient provideKubernetesClient() {
        return new KubernetesClientBuilder().build();
    }

    @Provides
    @Singleton
    public SecretsWorker provideSecretsWorker(KubernetesClient kubernetesClient) {
        return new SecretsWorker(kubernetesClient);
    }

    @Provides
    @Singleton
    public LHClustersWorker provideLHClustersWorker(
            K8sClientGateway k8sClientGateway, LittleHorseGrpc.LittleHorseBlockingStub lhClient) {
        return new LHClustersWorker(k8sClientGateway, lhClient);
    }

    @Provides
    @Singleton
    public LHTenantsWorker provideLHTenantWorker(K8sClientGateway k8sClientGateway) {
        return new LHTenantsWorker(k8sClientGateway);
    }

    @Provides
    @Singleton
    public LHCRWorker provideLHCRWorker(K8sClientGateway k8sClientGateway) {
        return new LHCRWorker(k8sClientGateway);
    }

    @Provides
    @Singleton
    public K8sClientGateway provideK8sClientGateway(KubernetesClient kubernetesClient) {
        return new K8sClientGateway(kubernetesClient);
    }
}
