package io.littlehorse.connector.exception;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.common.exception.LHTaskException;

import java.util.Optional;

/**
 * Unretryable exception indicating that the request was invalid. This can occur when the provided parameters are incorrect or when there is an issue with the Kubernetes API server. The workflow will not retry the task, and the error will be logged for further investigation.
 */
public class BadRequestException extends LHTaskException {
    public BadRequestException(final KubernetesClientException cause) {
        this(Optional.ofNullable(cause)
                .map(KubernetesClientException::getStatus)
                .map(Status::getMessage)
                .or(() -> Optional.ofNullable(cause).map(KubernetesClientException::getMessage))
                .orElse("Bad request"));
    }

    public BadRequestException(final String message) {
        super("bad-request", message);
    }
}
