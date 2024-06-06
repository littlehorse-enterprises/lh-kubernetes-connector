package io.littlehorse.agentworker.di;

import dagger.Module;
import dagger.Provides;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.HealthController;
import io.littlehorse.agentworker.workers.SecretsWorker;
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
}
