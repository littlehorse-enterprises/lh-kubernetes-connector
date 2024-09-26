package io.littlehorse.agentworker;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.config.AgentWorkerConfig;
import io.littlehorse.agentworker.config.ConfigLoader;
import io.littlehorse.agentworker.infra.HealthCheck;
import io.littlehorse.agentworker.infra.ShutdownHook;
import io.littlehorse.agentworker.workers.ApplyYamlTask;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.littlehorse.agentworker.workers.ApplyYamlTask.CREATE_OR_UPDATE_RESOURCE;

@Slf4j
public class Main {

    public static void main(String[] args) {
        AgentWorkerConfig config = ConfigLoader.getConfig();
        LHConfig lhConfig = new LHConfig();
        HealthCheck healthCheck = new HealthCheck(config.restPort());
        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();

        LHTaskWorker applyYamlTask = new LHTaskWorker(new ApplyYamlTask(kubernetesClient), CREATE_OR_UPDATE_RESOURCE, lhConfig,
                Map.of("cluster-id", config.clusterId()));

        startTaskWorkers(healthCheck, applyYamlTask);
    }

    private static void startTaskWorkers(HealthCheck healthCheck, LHTaskWorker... workers) {
        for (LHTaskWorker worker : workers) {
            ShutdownHook.add(worker.getTaskDefName(), worker);
            healthCheck.add(worker);
            worker.registerTaskDef();
            worker.start();
        }
    }
}
