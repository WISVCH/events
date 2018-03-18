package ch.wisv.events.core.exception.normal;

import ch.wisv.events.core.exception.LogLevelEnum;

public class UndefinedPaymentMethodOrderException extends EventsException {

    public UndefinedPaymentMethodOrderException() {
        super(LogLevelEnum.ERROR, "Undefined payment method in Order");
    }
}
