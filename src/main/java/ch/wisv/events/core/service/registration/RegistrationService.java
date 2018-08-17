package ch.wisv.events.core.service.registration;

import ch.wisv.events.core.exception.normal.RegistrationInvalidException;
import ch.wisv.events.core.model.registration.Registration;

/**
 * RegistrationService interface.
 */
public interface RegistrationService {

    /**
     * Add a new customer.
     *
     * @param registration customer model
     * @throws RegistrationInvalidException when Registration object is invalid.
     */
    void create(Registration registration) throws RegistrationInvalidException;
}
