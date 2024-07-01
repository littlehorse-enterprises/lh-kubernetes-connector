package io.littlehorse.agentworker.di;

import dagger.Module;
import dagger.Provides;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.HealthController;
import io.littlehorse.agentworker.workers.*;
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
    public LHClustersWorker provideLHClustersWorker(KubernetesClient kubernetesClient) {
        return new LHClustersWorker(kubernetesClient);
    }

    @Provides
    @Singleton
    public LHPrincipalsWorker provideLHPrincipalsWorker(K8sClientGateway k8sClientGateway) {
        return new LHPrincipalsWorker(k8sClientGateway);
    }

    @Provides
    @Singleton
    public LHTenantsWorker provideLHTenantWorker(K8sClientGateway k8sClientGateway) {
        return new LHTenantsWorker(k8sClientGateway);
    }

    @Provides
    @Singleton
    public K8sClientGateway provideK8sClientGateway(KubernetesClient kubernetesClient) {
        return new K8sClientGateway(kubernetesClient);
    }
}
