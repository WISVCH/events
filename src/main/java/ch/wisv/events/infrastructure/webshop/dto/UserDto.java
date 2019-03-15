package ch.wisv.events.infrastructure.webshop.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * UserDto.
 */
@Data
public class UserDto {

    /**
     * Name of the User.
     */
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    /**
     * Email of the User.
     */
    @Email(message = "Not a well-formed email address")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
}
