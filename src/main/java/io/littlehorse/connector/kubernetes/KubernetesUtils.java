package io.littlehorse.connector.kubernetes;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.Optional;

public final class KubernetesUtils {
    private enum KubernetesClientExceptionReason {
        AlreadyExists,
        BadRequest,
        Forbidden
    }

    private static final String RESOURCE_DEFINITION_NOT_FOUND_MESSAGE =
            "Could not find the metadata for the given apiVersion and kind";

    private static final String HANDLER_NOT_FOUND = "Could not find a registered handler for item";

    private KubernetesUtils() {}

    public static boolean isAlreadyExistsException(final KubernetesClientException e) {
        return isThisReason(KubernetesClientExceptionReason.AlreadyExists, e);
    }

    public static boolean isForbiddenException(final KubernetesClientException e) {
        return isThisReason(KubernetesClientExceptionReason.Forbidden, e);
    }

    public static boolean isBadRequestException(final KubernetesClientException e) {
        return Optional.ofNullable(e)
                        .map(KubernetesClientException::getMessage)
                        .map(message -> message.contains(RESOURCE_DEFINITION_NOT_FOUND_MESSAGE)
                                || message.contains(HANDLER_NOT_FOUND))
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
