package io.littlehorse.infrastructure.kubernetes;

import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import org.junit.jupiter.api.Test;

class KubernetesUtilsTest {

    @Test
    void shouldReturnTrueIfBadRequest() {
        assertTrue(KubernetesUtils.isBadRequestException(newException("BadRequest")));
    }

    @Test
    void shouldReturnFalseIfNotBadRequest() {
        assertFalse(KubernetesUtils.isBadRequestException(newException("NotBadRequest")));
    }

    @Test
    void shouldReturnFalseIfNotBadRequestForUnknownResourceDefinition() {
        assertTrue(
                KubernetesUtils.isBadRequestException(
                        new KubernetesClientException(
                                "Could not find the metadata for the given apiVersion and kind, please pass a ResourceDefinitionContext instead")));
    }

    @Test
    void shouldReturnTrueIfAlreadyExists() {
        assertTrue(KubernetesUtils.isAlreadyExistsException(newException("AlreadyExists")));
    }

    @Test
    void shouldReturnFalseIfNotAlreadyExists() {
        assertFalse(KubernetesUtils.isAlreadyExistsException(newException("NotAlreadyExists")));
    }

    private static KubernetesClientException newException(String reason) {
        return new KubernetesClientException(newStatus(reason));
    }

    private static Status newStatus(String reason) {
        return new StatusBuilder()
                .withReason(reason)
                .withCode(0)
                .withMessage("")
                .build();
    }
}
