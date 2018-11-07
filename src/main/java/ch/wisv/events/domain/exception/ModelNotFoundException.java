package ch.wisv.events.domain.exception;

import lombok.extern.java.Log;

/**
 * ModelNotFound exception.
 */
@Log
public class ModelNotFoundException extends RuntimeException {

    /**
     * ModelNotFoundException constructor.
     *
     * @param model of type Class
     * @param query of type Object
     */
    public ModelNotFoundException(Class model, Object query) {
        log.info(model.getName() + ": Model not found by query = '" + query + "'");
    }
}
