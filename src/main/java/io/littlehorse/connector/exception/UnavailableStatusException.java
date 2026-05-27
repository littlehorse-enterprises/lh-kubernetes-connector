package io.littlehorse.connector.exception;

/**
 * Retryable exception indicating that the status of the resource is currently unavailable. This can occur when the Kubernetes API server is temporarily unavailable or when there are network issues. The workflow will automatically retry the task after a delay, allowing time for the issue to be resolved.
 */
public class UnavailableStatusException extends RuntimeException {
    public UnavailableStatusException(final String message) {
        super(message);
    }
}
