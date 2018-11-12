package ch.wisv.events.infrastructure.execption.controller;

import ch.wisv.events.domain.exception.DocumentStorageException;
import ch.wisv.events.domain.exception.ModelNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SharedExceptionHandler controller.
 */
@Controller
@ControllerAdvice
public class SharedExceptionHandler {

    /**
     * Handle page not found exceptions.
     *
     * @param request of type HttpServletRequest
     *
     * @return String
     */
    @ExceptionHandler({
            ModelNotFoundException.class,
            DocumentStorageException.class
    })
    public String handleNotFoundExceptions(HttpServletRequest request) {
        return this.getPageNotFound(request);
    }

    /**
     * Request mapping of everything.
     *
     * @param request of type HttpServletRequest
     *
     * @return String
     */
    @RequestMapping({
            "/administrator/**",
            "/webshop/**",
    })
    public String handlePageNotFound(HttpServletRequest request) {
        return this.getPageNotFound(request);
    }

    /**
     * Get page not found template.
     *
     * @param request of type HttpServletRequest
     *
     * @return String
     */
    private String getPageNotFound(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();

        if (StringUtils.contains(requestURL, "administrator")) {
            return "admin/error/not-found";
        }

        return "error/not-found";
    }
}
