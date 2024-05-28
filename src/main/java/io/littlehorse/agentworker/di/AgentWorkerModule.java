package io.littlehorse.agentworker.di;

import dagger.Module;
import dagger.Provides;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.littlehorse.agentworker.HealthController;
import io.littlehorse.agentworker.workers.HorsePowerCalculator;
import io.littlehorse.agentworker.workers.LHClustersWorker;
import io.littlehorse.operator.LHOperatorConfig;
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
    public HorsePowerCalculator provideHorsePowerCalculator() {
        return new HorsePowerCalculator();
    }

    @Provides
    @Singleton
    public LHOperatorConfig provideLHOperatorConfig() {
        return new LHOperatorConfig();
    }

    @Provides
    @Singleton
    public KubernetesClient provideKubernetesClient(LHOperatorConfig lhOperatorConfig) {
        return lhOperatorConfig.getClient();
    }

    @Provides
    @Singleton
    public LHClustersWorker provideLHClustersWorker() {
        return new LHClustersWorker(new LHOperatorConfig().getClient(), new HorsePowerCalculator());
    }
}
