package io.littlehorse.connector.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
class ApplyTaskTest {

    @Mock
    KubernetesService service;

    @Test
    void shouldThrowBadRequestIfKubernetesClientExceptionIsBadRequest() {
        Status status = new StatusBuilder()
                .withReason("BadRequest")
                .withCode(0)
                .withMessage("")
                .build();
        KubernetesClientException exception = new KubernetesClientException(status);
        doThrow(exception).when(service).apply(anyString());

        ApplyTask task = new ApplyTask(service);

        assertThrows(BadRequestException.class, () -> task.apply(""));
    }
}
