package io.littlehorse.agentworker.di;

import dagger.Component;
import io.littlehorse.agentworker.HealthController;
import io.littlehorse.agentworker.workers.LHClustersWorker;
import io.littlehorse.agentworker.workers.SecretsWorker;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AgentWorkerModule.class})
public interface AgentWorkerComponent {
    LHConfig getLhConfig();

    LittleHorseGrpc.LittleHorseBlockingStub getLittleHorseBlockingStub();

    HealthController getHealthController();

    SecretsWorker getSecretsWorker();

    LHClustersWorker getLHClustersWorker();
}
