package io.littlehorse.connector.exception;

import io.littlehorse.sdk.common.exception.LHTaskException;

public class NotFoundException extends LHTaskException {
    public NotFoundException(final String message) {
        super("not-found", message);
    }
}
