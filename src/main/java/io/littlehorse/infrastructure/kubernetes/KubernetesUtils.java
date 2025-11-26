package io.littlehorse.infrastructure.kubernetes;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.Optional;

public final class KubernetesUtils {
    private enum KubernetesClientExceptionReason {
        AlreadyExists,
        BadRequest
    }

    private static final String RESOURCE_DEFINITION_NOT_FOUND_MESSAGE =
            "Could not find the metadata for the given apiVersion and kind";

    private KubernetesUtils() {}

    public static boolean isAlreadyExistsException(final KubernetesClientException e) {
        return isThisReason(KubernetesClientExceptionReason.AlreadyExists, e);
    }

    public static boolean isBadRequestException(final KubernetesClientException e) {
        return Optional.ofNullable(e)
                        .map(KubernetesClientException::getMessage)
                        .map(status -> status.contains(RESOURCE_DEFINITION_NOT_FOUND_MESSAGE))
                        .orElse(false)
                || isThisReason(KubernetesClientExceptionReason.BadRequest, e);
    }

    private static boolean isThisReason(
            final KubernetesClientExceptionReason exceptionReason,
            final KubernetesClientException e) {
        return Optional.ofNullable(e)
                .map(KubernetesClientException::getStatus)
                .map(Status::getReason)
                .map(reason -> reason.equals(exceptionReason.name()))
                .orElse(false);
    }
}
