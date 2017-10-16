package ch.wisv.events.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResponseEntityBuilder.
 */
public class ResponseEntityBuilder {

    /**
     * Create a standard response for all requests to the API
     *
     * @param httpStatus  The HTTP Status of the response
     * @param httpHeaders Optional Http Headers for the response.
     * @param message     The message in human readable String format
     * @param object      Optional object related to the request (like a created Customer)
     * @return The ResponseEntity in standard CH Events format.
     */
    private static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, HttpHeaders httpHeaders,
            String message,
            Object object
    ) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", httpStatus.toString());
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("message", message);
        responseBody.put("object", object);

        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        return new ResponseEntity<>(responseBody, httpHeaders, httpStatus);
    }

    /**
     * Create an ResponseEntity by HTTPStatus, message and Object
     *
     * @param httpStatus The HTTP Status of the message
     * @param message    The message in human readable String format
     * @param object     Object related to the request
     * @return The ResponseEntity in standard CH Events format.
     */
    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, String message, Object object) {
        return createResponseEntity(httpStatus, null, message, object);
    }

    /**
     * Create an ResponseEntity by HTTPStatus, message
     *
     * @param httpStatus The HTTP Status of the message
     * @param message    The message in human readable String format
     * @return The ResponseEntity in standard CH Events format.
     */
    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, String message) {
        return createResponseEntity(httpStatus, null, message, null);
    }

    /**
     * Create an ResponseEntity by HTTPStatus, HTTPHeaders and message
     *
     * @param httpStatus  The HTTP Status of the message
     * @param httpHeaders Http Headers for the response.
     * @param message     The message in human readable String format
     * @return The ResponseEntity in standard CH Events format.
     */
    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, HttpHeaders httpHeaders,
            String message
    ) {
        return createResponseEntity(httpStatus, httpHeaders, message, null);
    }
}
