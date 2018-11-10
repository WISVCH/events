package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.validator.UserValidator;
import ch.wisv.events.services.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * AdminUserController.
 */
@Controller
@RequestMapping("/administrator/users")
public class AdminUserController extends AbstractAdminController<User> {

    /**
     * AdminUserController constructor.
     *
     * @param userService of type UserService
     */
    @Autowired
    public AdminUserController(UserService userService) {
        super(userService, new User(), "users", "user");
    }

    /**
     * Add the specific UserValidator.
     *
     * @param binder of type WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new UserValidator((UserService) service));
    }

    /**
     * Save a file.
     *
     * @param model of type AbstractModel
     * @param file  of type MultipartFile
     *
     * @return T
     */
    @Override
    User saveFile(User model, MultipartFile file) {
        return model;
    }

    /**
     * Add Model to the index page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeIndex() {
        return null;
    }

    /**
     * Add Model to the view page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeView() {
        return null;
    }

    /**
     * Add Model to the edit page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeCreateEdit() {
        return null;
    }
}
