package io.littlehorse.connector.kubernetes;

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
        KubernetesClientException clientException = new KubernetesClientException(
                "Could not find the metadata for the given apiVersion and kind, please pass a ResourceDefinitionContext instead");
        assertTrue(KubernetesUtils.isBadRequestException(clientException));
    }

    @Test
    void shouldReturnTrueIfAlreadyExists() {
        assertTrue(KubernetesUtils.isAlreadyExistsException(newException("AlreadyExists")));
    }

    @Test
    void shouldReturnFalseIfNotAlreadyExists() {
        assertFalse(KubernetesUtils.isAlreadyExistsException(newException("NotAlreadyExists")));
    }

    @Test
    void shouldReturnTrueIfForbidden() {
        assertTrue(KubernetesUtils.isForbiddenException(newException("Forbidden")));
    }

    @Test
    void shouldReturnFalseIfNotForbidden() {
        assertFalse(KubernetesUtils.isForbiddenException(newException("NotForbidden")));
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
