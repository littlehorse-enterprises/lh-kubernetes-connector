package io.littlehorse.agentworker;

import static io.littlehorse.agentworker.workers.ApplyYamlTask.CREATE_OR_UPDATE_RESOURCE;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.config.AgentWorkerConfig;
import io.littlehorse.agentworker.config.ConfigLoader;
import io.littlehorse.agentworker.infra.HealthCheck;
import io.littlehorse.agentworker.infra.ShutdownHook;
import io.littlehorse.agentworker.workers.ApplyYamlTask;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        AgentWorkerConfig config = ConfigLoader.getConfig();
        LHConfig lhConfig = new LHConfig();
        HealthCheck healthCheck = new HealthCheck(config.restPort());
        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();

        LHTaskWorker applyYamlTask = new LHTaskWorker(
                new ApplyYamlTask(kubernetesClient),
                CREATE_OR_UPDATE_RESOURCE,
                lhConfig,
                Map.of("k8s-cluster-id", config.k8sClusterId()));

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
