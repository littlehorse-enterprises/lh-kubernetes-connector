package io.littlehorse.agentworker.infra;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthCheck {

    private final List<LHTaskWorker> workers = new ArrayList<>();

    public HealthCheck(int port) {
        Javalin server = Javalin.create().get("/health", this::health).start("0.0.0.0", port);
        ShutdownHook.add("HealthCheck", server::stop);
        log.info("HealthCheck server started on port {}", port);
    }

    private static boolean isHealthy(LHTaskWorker lhTaskWorker) {
        try {
            return lhTaskWorker.healthStatus().isHealthy();
        } catch (Exception e) {
            log.error("Worker %s unhealthy".formatted(lhTaskWorker.getTaskDefName()), e);
            return false;
        }
    }

    public void add(LHTaskWorker worker) {
        if (Objects.nonNull(worker)) {
            workers.add(worker);
        }
    }

    public void health(Context context) {
        boolean isHealthy = workers.stream().allMatch(HealthCheck::isHealthy);
        context.status(isHealthy ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
