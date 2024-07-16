package io.littlehorse.agentworker.workers;

import lombok.Getter;

@Getter
public class ClusterHealthInfo {
    private ClusterStatus clusterStatus;
    private String errorDescription;

    public ClusterHealthInfo(ClusterStatus clusterStatus) {
        this.clusterStatus = clusterStatus;
    }

    public ClusterHealthInfo(ClusterStatus clusterStatus, String errorDescription) {
        this.clusterStatus = clusterStatus;
        this.errorDescription = errorDescription;
    }
}
