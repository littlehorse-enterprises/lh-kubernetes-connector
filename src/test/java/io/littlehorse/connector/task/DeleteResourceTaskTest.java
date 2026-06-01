package io.littlehorse.connector.task;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.service.KubernetesService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteResourceTaskTest {

    @Mock
    KubernetesService service;

    @Test
    void shouldThrowBadRequestExceptionWhenRequiredFieldIsBlank() {
        DeleteResourceTask task = new DeleteResourceTask(service);

        assertThrows(
                BadRequestException.class, () -> task.delete(null, "Secret", null, "my-secret"));
        assertThrows(BadRequestException.class, () -> task.delete("v1", null, null, "my-secret"));
        assertThrows(BadRequestException.class, () -> task.delete("v1", "Secret", null, null));
    }

    @Test
    void shouldThrowBadRequestIfKubernetesClientExceptionIsBadRequest() {
        Status status = new StatusBuilder()
                .withReason("BadRequest")
                .withCode(0)
                .withMessage("")
                .build();
        KubernetesClientException exception = new KubernetesClientException(status);
        doThrow(exception).when(service).delete(anyString(), anyString(), isNull(), anyString());

        DeleteResourceTask task = new DeleteResourceTask(service);

        assertThrows(
                BadRequestException.class, () -> task.delete("v1", "Secret", null, "my-secret"));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenThereIsNotCRD() {
        KubernetesClientException exception = new KubernetesClientException(
                "Could not find the metadata for the given apiVersion and kind, please pass a ResourceDefinitionContext instead");
        doThrow(exception).when(service).delete(anyString(), anyString(), isNull(), anyString());

        DeleteResourceTask task = new DeleteResourceTask(service);
        assertThrows(
                BadRequestException.class,
                () -> task.delete("v1", "NotCRD", null, "not-a-resource"));
    }
}
