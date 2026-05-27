package io.littlehorse.connector.health;

import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.littlehorse.quarkus.runtime.health.LHTaskStatus;
import io.littlehorse.sdk.worker.LHTaskWorkerHealthReason;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class ConnectorHealthCheck implements HealthCheck {

    private final LHTaskStatusesContainer taskStatusesContainer;

    public ConnectorHealthCheck(final LHTaskStatusesContainer taskStatusesContainer) {
        this.taskStatusesContainer = taskStatusesContainer;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("LittleHorse Kubernetes Connector")
                .status(isHealthy())
                .build();
    }

    private boolean isHealthy() {
        try {
            return taskStatusesContainer.getTaskStatuses().stream().allMatch(this::isHealthy);
        } catch (final Exception e) {
            return false;
        }
    }

    private boolean isHealthy(final LHTaskStatus lhTaskStatus) {
        return !LHTaskWorkerHealthReason.UNHEALTHY.equals(lhTaskStatus.getStatus());
    }
}
