package ch.wisv.events.domain.exception;

/**
 * DocumentStorageException.
 */
public class DocumentStorageException extends RuntimeException {

    /**
     * DocumentStorageException constructor.
     *
     * @param message of type String
     */
    public DocumentStorageException(String message) {
        super(message);
    }
}
