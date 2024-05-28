package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Data;

@Data
public class HorsePower {
    private Quantity volumeSize;
    private int replicationFactor;
    private int clusterPartitions;
    private int replicas;

    public HorsePower(Quantity volumeSize, int replicationFactor, int clusterPartitions, int replicas) {
        this.volumeSize = volumeSize;
        this.replicationFactor = replicationFactor;
        this.clusterPartitions = clusterPartitions;
        this.replicas = replicas;
    }

    private HorsePower() {}
}
