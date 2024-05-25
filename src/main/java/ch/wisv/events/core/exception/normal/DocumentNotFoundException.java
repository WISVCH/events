package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

/**
 * DocumentNotFoundException class.
 */
public class DocumentNotFoundException extends EventsException {

    /**
     * DocumentNotFoundException.
     *
     * @param message of type String
     */
    public DocumentNotFoundException(String message) {
        super(LogLevelEnum.WARN, "Document with " + message + " not found!");
    }
}
