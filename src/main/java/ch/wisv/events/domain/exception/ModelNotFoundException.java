package ch.wisv.events.domain.exception;

import lombok.extern.java.Log;

/**
 * ModelNotFound exception.
 */
@Log
public class ModelNotFoundException extends RuntimeException {

    /** Error message template. */
    private static final String MESSAGE = "%s: Model not found by query = '%s'";

    /** Errors simple message template. */
    private static final String SIMPLE_MESSAGE = "One or more %ss does not exists";

    /**
     * ModelNotFoundException constructor.
     *
     * @param model of type Class
     * @param query of type Object
     */
    public ModelNotFoundException(Class model, Object query) {
        super(String.format(SIMPLE_MESSAGE, model.getSimpleName()));

        log.info(String.format(MESSAGE, model.getName(), query));
    }
}
