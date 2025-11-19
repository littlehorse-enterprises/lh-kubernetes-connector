package io.littlehorse.connector.exception;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.sdk.common.exception.LHTaskException;

public class BadRequestException extends LHTaskException {
    public BadRequestException(final KubernetesClientException cause) {
        super("bad-request", cause.getStatus().getMessage());
    }
}
