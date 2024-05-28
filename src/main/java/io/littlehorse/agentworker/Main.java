package io.littlehorse.agentworker;

import io.littlehorse.agentworker.di.AgentWorkerComponent;
import io.littlehorse.agentworker.di.DaggerAgentWorkerComponent;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final int REST_PORT = 9080;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        AgentWorkerComponent agentWorkerComponent = DaggerAgentWorkerComponent.create();
        LittleHorseGrpc.LittleHorseBlockingStub littleHorseBlockingStub =
                agentWorkerComponent.getLittleHorseBlockingStub();

        List<LHTaskWorker> lhWorkers = List.of(new LHTaskWorker(
                agentWorkerComponent.getLhClustersWorker(),
                "create-cluster-in-fake-org",
                agentWorkerComponent.getLhConfig()));

        startTaskWorkers(lhWorkers);
    }

    private static void startTaskWorkers(List<LHTaskWorker> lhWorkers) throws IOException {
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> lhWorkers.forEach(worker -> {
                    logger.debug("Closing {}", worker.getTaskDefName());
                    worker.close();
                })));

        for (LHTaskWorker lhWorker : lhWorkers) {
            lhWorker.registerTaskDef();
            lhWorker.start();
        }
    }
}
