package io.littlehorse.connector.exception;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.common.exception.LHTaskException;

import java.util.Optional;

/**
 * Unretryable exception indicating that the task execution failed due to insufficient permissions to perform the requested operation.
 */
public class ForbiddenException extends LHTaskException {
    public ForbiddenException(final KubernetesClientException cause) {
        this(Optional.ofNullable(cause)
                .map(KubernetesClientException::getStatus)
                .map(Status::getMessage)
                .or(() -> Optional.ofNullable(cause).map(KubernetesClientException::getMessage))
                .orElse("Forbidden"));
    }

    public ForbiddenException(final String message) {
        super("forbidden", message);
    }
}
