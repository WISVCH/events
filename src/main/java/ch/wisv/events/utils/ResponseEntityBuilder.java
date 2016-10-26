package ch.wisv.events.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sven on 20/10/2016.
 */
public class ResponseEntityBuilder {
    /**
     * Create a standard response for all requests to the API
     *
     * @param httpStatus  The HTTP Status of the response
     * @param httpHeaders Optional Http Headers for the response.
     * @param message     The message in human readable String format
     * @param object      Optional object related to the request (like a created User)
     * @return The ResponseEntity in standard Area FiftyLAN format.
     */
    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, HttpHeaders httpHeaders, String message,
                                                         Object object) {
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

    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, String message, Object object) {
        return createResponseEntity(httpStatus, null, message, object);
    }

    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, String message) {
        return createResponseEntity(httpStatus, null, message, null);
    }

    public static ResponseEntity<?> createResponseEntity(HttpStatus httpStatus, HttpHeaders httpHeaders,
                                                         String message) {
        return createResponseEntity(httpStatus, httpHeaders, message, null);
    }
}
