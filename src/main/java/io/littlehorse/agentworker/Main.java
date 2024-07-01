package io.littlehorse.agentworker;

import io.littlehorse.agentworker.di.AgentWorkerComponent;
import io.littlehorse.agentworker.di.DaggerAgentWorkerComponent;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final int REST_PORT = 9080;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        String dataPlaneId = System.getenv().get("AW_DATA_PLANE_ID");

        if (dataPlaneId == null || dataPlaneId.isEmpty()) {
            throw new IllegalArgumentException(
                    "You must provide the AW_DATA_PLANE_ID environment variable for the Agent Worker to work properly.");
        }

        AgentWorkerComponent agentWorkerComponent = DaggerAgentWorkerComponent.create();

        startTaskWorkers(List.of(
                new LHTaskWorker(
                        agentWorkerComponent.getSecretsWorker(),
                        "create-introspection-secret-for-cluster-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId)),
                new LHTaskWorker(
                        agentWorkerComponent.getLHPrincipalsWorker(),
                        "create-human-principal-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId)),
                new LHTaskWorker(
                        agentWorkerComponent.getSecretsWorker(),
                        "create-secret-for-lh-dashboard-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId)),
                new LHTaskWorker(
                        agentWorkerComponent.getLHClustersWorker(),
                        "create-lh-cluster-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId)),
                new LHTaskWorker(
                        agentWorkerComponent.getLHTenantsWorker(),
                        "create-lh-tenant-in-dp-${data-plane-id}",
                        agentWorkerComponent.getLhConfig(),
                        Map.of("data-plane-id", dataPlaneId))));
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
