package ch.wisv.events.core.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum LogLevelEnum {

    ERROR, WARN, INFO, DEBUG;

    /**
     * Log level enum.
     *
     * @param message of type String.
     */
    public void logMessage(String message) {
        switch (this) {
            case ERROR:
                log.error(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case INFO:
                log.info(message);
                break;
            case DEBUG:
            default:
                log.debug(message);
                break;
        }
    }
}
