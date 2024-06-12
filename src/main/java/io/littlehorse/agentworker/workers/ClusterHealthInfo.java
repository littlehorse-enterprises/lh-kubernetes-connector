package io.littlehorse.agentworker.workers;

import lombok.Getter;

@Getter
public class ClusterHealthInfo {
    private ClusterStatus clusterStatus;
    private int horsePower;
    private String errorDescription;

    public ClusterHealthInfo(ClusterStatus clusterStatus, int horsepower) {
        this.clusterStatus = clusterStatus;
        this.horsePower = horsepower;
    }

    public ClusterHealthInfo(ClusterStatus clusterStatus, String errorDescription) {
        this.clusterStatus = clusterStatus;
        this.errorDescription = errorDescription;
    }
}
