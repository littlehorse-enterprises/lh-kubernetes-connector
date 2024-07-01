package io.littlehorse.agentworker.workers;

public enum LHResources {
    LH_CLUSTER("LHCluster"),
    LH_PRINCIPAL("LHPrincipal"),
    LH_TENANT("LHTenant");

    private final String description;

    LHResources(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
