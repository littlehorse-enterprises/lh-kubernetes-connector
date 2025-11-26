package io.littlehorse.infrastructure.kubernetes;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.KubernetesClientException;

import java.util.Optional;

public final class KubernetesUtils {
    private static final String ALREADY_EXISTS_ERROR_CODE = "AlreadyExists";
    private static final String BAD_REQUEST_ERROR_CODE = "BadRequest";
    private static final String RESOURCE_DEFINITION_NOT_FOUND_MESSAGE =
            "Could not find the metadata for the given apiVersion and kind";

    private KubernetesUtils() {}

    public static boolean isAlreadyExistsException(final KubernetesClientException e) {
        return isException(e, ALREADY_EXISTS_ERROR_CODE);
    }

    public static boolean isBadRequestException(final KubernetesClientException e) {
        return Optional.ofNullable(e)
                        .map(KubernetesClientException::getMessage)
                        .map(status -> status.contains(RESOURCE_DEFINITION_NOT_FOUND_MESSAGE))
                        .orElse(false)
                || isException(e, BAD_REQUEST_ERROR_CODE);
    }

    private static boolean isException(
            final KubernetesClientException e, final String expectedReason) {
        return Optional.ofNullable(e)
                .map(KubernetesClientException::getStatus)
                .map(Status::getReason)
                .map(reason -> reason.equals(expectedReason))
                .orElse(false);
    }
}
