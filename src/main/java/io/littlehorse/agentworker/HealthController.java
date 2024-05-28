package io.littlehorse.agentworker;

import io.javalin.http.Context;
import io.littlehorse.agentworker.model.HealthResponse;
import io.littlehorse.agentworker.model.HealthStatus;

public class HealthController {
    public void liveness(Context context) {
        context.json(new HealthResponse(HealthStatus.OK));
    }
}
