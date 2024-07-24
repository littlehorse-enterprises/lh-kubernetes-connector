package io.littlehorse.agentworker;

import com.google.protobuf.Empty;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.littlehorse.agentworker.model.HealthResponse;
import io.littlehorse.agentworker.model.HealthStatus;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    private final LittleHorseGrpc.LittleHorseBlockingStub lhClient;

    public HealthController(LittleHorseGrpc.LittleHorseBlockingStub lhClient) {
        this.lhClient = lhClient;
    }

    public void liveness(Context context) {
        try {
            lhClient.whoami(Empty.newBuilder().build());
            context.json(new HealthResponse(HealthStatus.OK, ""));
        } catch (Exception e) {
            logger.error("Liveness probe error on the Agent Worker.", e);
            context.json(new HealthResponse(HealthStatus.UNHEALTHY, e.getCause().getMessage()));
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
