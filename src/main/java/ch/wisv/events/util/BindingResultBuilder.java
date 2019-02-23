package ch.wisv.events.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * BindingResultBuilder.
 */
public final class BindingResultBuilder {

    /**
     * Private constructor.
     */
    private BindingResultBuilder() {
    }

    /**
     * Convert BindingResults into ErrorMap.
     *
     * @param bindingResult of type BindingResult
     * @return Map
     */
    public static Map<String, String> createErrorMap(BindingResult bindingResult) {
        Map<String, String> errorMessages = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorMessages;
    }
}
