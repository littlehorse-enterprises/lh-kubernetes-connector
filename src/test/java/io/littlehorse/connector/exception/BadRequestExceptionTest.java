package io.littlehorse.connector.exception;

import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.StatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;

import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

    @Test
    void returnDefaultMessageIfInputIsNull() {
        BadRequestException badRequestException =
                new BadRequestException((KubernetesClientException) null);
        assertEquals("Bad request", badRequestException.getMessage());
    }

    @Test
    void returnStatusMessage() {
        String expectedMessage = "expected message";
        BadRequestException badRequestException =
                new BadRequestException(new KubernetesClientException(new StatusBuilder()
                        .withMessage(expectedMessage)
                        .withCode(0)
                        .build()));
        assertEquals(expectedMessage, badRequestException.getMessage());
    }

    @Test
    void returnMessage() {
        String expectedMessage = "expected message";
        BadRequestException badRequestException =
                new BadRequestException(new KubernetesClientException(expectedMessage));
        assertEquals(expectedMessage, badRequestException.getMessage());
    }
}
