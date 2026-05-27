package io.littlehorse.connector.exception;

/**
 * Retryable exception indicating that the requested resource was not found. This can occur when the resource has been deleted or when there is a delay in the Kubernetes API server reflecting the creation of a new resource. The workflow will automatically retry the task after a delay, allowing time for the resource to become available.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
