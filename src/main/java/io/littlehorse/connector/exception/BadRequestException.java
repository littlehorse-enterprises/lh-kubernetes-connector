package io.littlehorse.connector.exception;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.common.exception.LHTaskException;

import java.util.Optional;

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
