package io.littlehorse.agentworker.workers;

import lombok.Getter;

@Getter
public class ClusterHealthInfo {
    private final ClusterStatus clusterStatus;
    private final String message;

    public ClusterHealthInfo(ClusterStatus clusterStatus) {
        this.clusterStatus = clusterStatus;
        message = "";
    }

    public ClusterHealthInfo(ClusterStatus clusterStatus, String message) {
        this.clusterStatus = clusterStatus;
        this.message = message;
    }
}
