package io.littlehorse.agentworker;

import io.javalin.Javalin;
import io.littlehorse.agentworker.di.AgentWorkerComponent;
import io.littlehorse.agentworker.di.DaggerAgentWorkerComponent;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int REST_PORT = 8091;

    public static void main(String[] args) {
        String dataPlaneId = System.getenv().get("AW_DATA_PLANE_ID");

        if (dataPlaneId == null || dataPlaneId.isEmpty()) {
            throw new IllegalArgumentException(
                    "You must provide the AW_DATA_PLANE_ID environment variable for the Agent Worker to work properly.");
        }

        AgentWorkerComponent agentWorkerComponent = DaggerAgentWorkerComponent.create();

        logger.info("Starting Javalin server on port {}", REST_PORT);
        Javalin.create()
                .get("/health", agentWorkerComponent.getHealthController()::liveness)
                .start(REST_PORT);

        startTaskWorkers(List.of(
                new LHTaskWorker(
                        agentWorkerComponent.getLHCRWorker(),
                        "create-resource-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId)),
                new LHTaskWorker(
                        agentWorkerComponent.getLHClustersWorker(),
                        "create-lh-cluster-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId))));
    }

    private static void startTaskWorkers(List<LHTaskWorker> lhWorkers) {
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
