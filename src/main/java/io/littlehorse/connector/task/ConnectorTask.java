package io.littlehorse.connector.task;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.sdk.worker.LHTaskMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LHTask
public class ConnectorTask {
    private static Logger log = LoggerFactory.getLogger(ConnectorTask.class);
    private final KubernetesClient client;

    public ConnectorTask(KubernetesClient client) {
        this.client = client;
    }

    // TODO: define configuration structure and standard
    // TODO: define retriable and not retryable exceptions
    // TODO: define what to log (info and debug)
    @LHTaskMethod("${littlehorse.kubernetes.connector.name}")
    public void applyManifest(final String manifest) {}
}
