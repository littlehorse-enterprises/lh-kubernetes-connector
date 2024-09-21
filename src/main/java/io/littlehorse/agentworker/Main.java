package io.littlehorse.agentworker;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.littlehorse.agentworker.config.AgentWorkerConfig;
import io.littlehorse.agentworker.config.ConfigLoader;
import io.littlehorse.agentworker.infra.HealthCheck;
import io.littlehorse.agentworker.infra.ShutdownHook;
import io.littlehorse.agentworker.workers.ApplyYamlTask;
import io.littlehorse.agentworker.workers.CreateClusterTask;
import io.littlehorse.agentworker.workers.gateways.K8sClientGateway;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.littlehorse.agentworker.workers.ApplyYamlTask.CREATE_RESOURCE_IN_DP_DATA_PLANE_ID;
import static io.littlehorse.agentworker.workers.CreateClusterTask.CREATE_LH_CLUSTER_IN_DP_DATA_PLANE_ID;

@Slf4j
public class Main {

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        AgentWorkerConfig config = configLoader.getConfig();
        LHConfig lhConfig = new LHConfig();
        HealthCheck healthCheck = new HealthCheck(config.restPort());

        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();
        K8sClientGateway k8sClientGateway = new K8sClientGateway(kubernetesClient);

        Map<String, String> dataPlaneConfig = Map.of("data-plane-id", config.dataPlaneId());
        LHTaskWorker applyYamlTask = new LHTaskWorker(
                new ApplyYamlTask(k8sClientGateway),
                CREATE_RESOURCE_IN_DP_DATA_PLANE_ID,
                lhConfig,
                dataPlaneConfig);

        LHTaskWorker createClusterTask = new LHTaskWorker(
                new CreateClusterTask(k8sClientGateway),
                CREATE_LH_CLUSTER_IN_DP_DATA_PLANE_ID,
                lhConfig,
                dataPlaneConfig);

        startTaskWorkers(healthCheck, applyYamlTask, createClusterTask);
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
