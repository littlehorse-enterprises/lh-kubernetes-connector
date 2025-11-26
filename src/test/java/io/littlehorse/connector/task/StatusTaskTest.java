package io.littlehorse.connector.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.littlehorse.connector.exception.BadRequestException;
import io.littlehorse.connector.service.KubernetesService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatusTaskTest {

    @Mock
    KubernetesService service;

    @Test
    void shouldThrowBadRequestExceptionWhenRequeriedFieldIsNull() {
        StatusTask task = new StatusTask(service);

        assertThrows(BadRequestException.class, () -> task.status(null, "", null, ""));
        assertThrows(BadRequestException.class, () -> task.status("", null, null, ""));
        assertThrows(BadRequestException.class, () -> task.status("", "", null, null));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenThereIsNotCRD() {
        KubernetesClientException exception = new KubernetesClientException(
                "Could not find the metadata for the given apiVersion and kind, please pass a ResourceDefinitionContext instead");
        doThrow(exception).when(service).status(anyString(), anyString(), isNull(), anyString());

        StatusTask task = new StatusTask(service);
        assertThrows(
                BadRequestException.class, () -> task.status("v1", "NotCRD", null, "not-a-pod"));
    }
}
