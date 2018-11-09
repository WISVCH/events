package ch.wisv.events.domain.validator;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * UserValidator.
 */
@Component
public class UserValidator implements Validator {

    /**
     * UserRepository
     */
    private final UserService userService;

    /**
     * UserValidator constructor.
     *
     * @param userService of type UserRepository
     */
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    /**
     * Can this {@link Validator} {@link #validate(Object, Errors) validate}
     * instances of the supplied {@code clazz}?
     * <p>This method is <i>typically</i> implemented like so:
     * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
     * (Where {@code Foo} is the class (or superclass) of the actual
     * object instance that is to be {@link #validate(Object, Errors) validated}.)
     *
     * @param clazz the {@link Class} that this {@link Validator} is
     *              being asked if it can {@link #validate(Object, Errors) validate}
     *
     * @return {@code true} if this {@link Validator} can indeed
     *         {@link #validate(Object, Errors) validate} instances of the
     *         supplied {@code clazz}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    /**
     * Validate the supplied {@code target} object, which must be
     * of a {@link Class} for which the {@link #supports(Class)} method
     * typically has (or would) return {@code true}.
     * <p>The supplied {@link Errors errors} instance can be used to report
     * any resulting validation errors.
     *
     * @param target the object that is to be validated (can be {@code null})
     * @param errors contextual state about the validation process (never {@code null})
     *
     * @see ValidationUtils
     */
    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        try {
            User byEmail = userService.getByEmail(user.getEmail());
            if (!byEmail.getPublicReference().equals(user.getPublicReference())) {
                errors.rejectValue("email", "emailDuplicated", "Email address is already in use");
            }
        } catch (ModelNotFoundException ignored) {
        }

        try {
            User bySub = userService.getBySub(user.getSub());
            if (!bySub.getPublicReference().equals(user.getPublicReference())) {
                errors.rejectValue("sub", "subDuplicated", "WISVCH sub is already in use");
            }
        } catch (ModelNotFoundException ignored) {
        }
    }
}
