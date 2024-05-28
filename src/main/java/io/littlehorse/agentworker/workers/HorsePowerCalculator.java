package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.api.model.Quantity;

public class HorsePowerCalculator {
    public static final int BASE_VOLUME_SIZE_IN_G = 10;
    public static final int BASE_REPLICATION_FACTOR = 3;
    public static final int BASE_CLUSTER_PARTITIONS = 6;
    public static final int BASE_REPLICAS = 3;

    public HorsePowerCalculator() {}

    public HorsePower compute(int numberOfHorsePower) throws InvalidHorsePowerQuantityException {
        if (numberOfHorsePower <= 0) {
            throw new InvalidHorsePowerQuantityException("HorsePower quantity should be greater than zero.");
        }

        int calculatedReplicas = BASE_REPLICAS * numberOfHorsePower;

        return new HorsePower(
                new Quantity(BASE_VOLUME_SIZE_IN_G + "G"),
                BASE_REPLICATION_FACTOR,
                BASE_CLUSTER_PARTITIONS,
                calculatedReplicas);
    }
}
