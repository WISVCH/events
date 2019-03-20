package ch.wisv.events.services;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.exception.ThrowingFunction;
import static ch.wisv.events.domain.exception.ThrowingFunction.unchecked;
import ch.wisv.events.domain.model.user.LdapGroup;
import ch.wisv.events.domain.model.user.User;
import java.util.Objects;
import static java.util.Objects.nonNull;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

/**
 * AuthenticationService.
 */
@Service
public class AuthenticationService {

    /**
     * UserService.
     */
    private final UserService userService;

    /**
     * AuthenticationService constructor.
     *
     * @param userService of type UserService
     */
    @Autowired
    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the User that is currently logged in.
     *
     * @return User
     */
    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CHUserInfo userInfo = this.getChUserInfo(auth);

        User user = this.getUserByChUserInfo(userInfo);
        this.updateUserInfo(user, userInfo);

        return user;
    }

    /**
     * Get CHUserInfo from a Authentication object.
     *
     * @param auth of type Authentication.
     *
     * @return CHUserInfo
     */
    private CHUserInfo getChUserInfo(Authentication auth) {
        if (!(auth instanceof OIDCAuthenticationToken)) {
            throw new InvalidTokenException("Invalid authentication");
        }

        OIDCAuthenticationToken oidcToken = (OIDCAuthenticationToken) auth;

        if (!(oidcToken.getUserInfo() instanceof CHUserInfo)) {
            throw new InvalidTokenException("Invalid UserInfo object");
        }

        return (CHUserInfo) oidcToken.getUserInfo();
    }

    /**
     * Get a User by CHUserInfo.
     *
     * @param userInfo of type CHUserInfo.
     *
     * @return User
     */
    private User getUserByChUserInfo(CHUserInfo userInfo) {
        try {
            return userService.getBySub(userInfo.getSub());
        } catch (ModelNotFoundException ignored) {
        }

        try {
            return userService.getByEmail(userInfo.getEmail());
        } catch (ModelNotFoundException ignored) {
        }

        return userService.createByChUserInfo(userInfo);
    }

    /**
     * Update User Info with the information provided by CHUserInfo.
     *
     * @param user of type User.
     * @param userInfo of type CHUserInfo.
     */
    private void updateUserInfo(User user, CHUserInfo userInfo) {
        if (isEmpty(user.getSub())) {
            user.setSub(userInfo.getSub());
        }

        if (isEmpty(user.getEmail())) {
            user.setEmail(userInfo.getEmail());
        }

        user.setVerified(true);
        user.setLdapGroup(userInfo.getLdapGroups().stream()
                .map(ldapString -> LdapGroup.getByName(ldapString.toUpperCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        userService.save(user);
    }
}
