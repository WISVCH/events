package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.services.AbstractService;
import static java.lang.String.format;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AbstractAdminController class.
 *
 * @param <T> of type AbstractModel
 */
public abstract class AbstractAdminController<T extends AbstractModel> {

    /**
     * Path the the index view template.
     */
    private static final String DEFAULT_PATH_INDEX_VIEW = "admin/%s/index";

    /**
     * Path the the view view template.
     */
    private static final String DEFAULT_PATH_VIEW_VIEW = "admin/%s/view";

    /**
     * Path the the create/edit view template.
     */
    private static final String DEFAULT_PATH_CREATE_EDIT_VIEW = "admin/%s/%s";

    /**
     * Object name in plural.
     */
    static String OBJECT_PLURAL;

    /**
     * Object name in singular.
     */
    static String OBJECT_SIGNULAR;

    /**
     * AbstractService.
     */
    private AbstractService<T> service;

    /**
     * AbstractModel.
     */
    private T emptyModel;

    /**
     * AbstractAdminController constructor.
     *
     * @param service    of type AbstractService
     * @param emptyModel of type AbstractModel
     */
    AbstractAdminController(AbstractService<T> service, T emptyModel) {
        this.service = service;
        this.emptyModel = emptyModel;
    }

    /**
     * List all the events.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute(OBJECT_PLURAL, service.getAll());
        model.addAllAttributes(this.beforeIndex());

        return format(DEFAULT_PATH_INDEX_VIEW, OBJECT_PLURAL);
    }

    /**
     * View a specific Object.
     *
     * @param model           of type Model
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/view/{publicReference}")
    public String view(Model model, @PathVariable String publicReference) {
        model.addAttribute(OBJECT_SIGNULAR, service.getByPublicReference(publicReference));
        model.addAllAttributes(this.beforeView());

        return format(DEFAULT_PATH_VIEW_VIEW, OBJECT_PLURAL);
    }

    /**
     * Edit a specific Object.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping({"/create", "/edit/{publicReference}"})
    public String createEdit(Model model, @PathVariable(required = false) String publicReference) {
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new HashMap<String, String>());
        }
        if (!model.containsAttribute(OBJECT_SIGNULAR)) {
            if (isNotEmpty(publicReference)) {
                model.addAttribute(OBJECT_SIGNULAR, service.getByPublicReference(publicReference));
            } else {
                model.addAttribute(OBJECT_SIGNULAR, emptyModel);
            }
        }
        model.addAllAttributes(this.beforeCreateEdit());

        return format(DEFAULT_PATH_CREATE_EDIT_VIEW, OBJECT_PLURAL, OBJECT_SIGNULAR);
    }

    /**
     * Update an AbstractModel.
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     * @param file            of type MultipartFile
     * @param model           of type AbstractModel
     * @param bindingResult   of type BindingResult
     *
     * @return String
     */
    @PostMapping({"/create", "/edit/{publicReference}"})
    public String createEdit(
            RedirectAttributes redirect,
            @PathVariable(required = false) String publicReference,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @Valid @ModelAttribute T model,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMessages = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            redirect.addFlashAttribute("errors", errorMessages);
            redirect.addFlashAttribute(OBJECT_SIGNULAR, model);

            if (isNotEmpty(publicReference)) {
                return format("redirect:/administrator/%s/edit/%s", OBJECT_PLURAL, model.getPublicReference());
            }
            return format("redirect:/administrator/%s/create", OBJECT_PLURAL);
        }

        model = this.saveFile(model, file);
        service.save(model);

        return format("redirect:/administrator/%s/view/%s", OBJECT_PLURAL, model.getPublicReference());
    }

    /**
     * Delete a object.
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/delete/{publicReference}")
    public String delete(RedirectAttributes redirect, @PathVariable String publicReference) {
        T model = service.getByPublicReference(publicReference);
        service.delete(model);

        redirect.addFlashAttribute("success", OBJECT_SIGNULAR + " has been deleted");

        return "redirect:/administrator/" + OBJECT_PLURAL;
    }

    /**
     * Save a file.
     *
     * @param model of type AbstractModel
     * @param file  of type MultipartFile
     *
     * @return T
     */
    abstract T saveFile(T model, MultipartFile file);

    /**
     * Add Model to the index page.
     *
     * @return Map
     */
    abstract Map<String, ?> beforeIndex();

    /**
     * Add Model to the view page.
     *
     * @return Map
     */
    abstract Map<String, ?> beforeView();

    /**
     * Add Model to the edit page.
     *
     * @return Map
     */
    abstract Map<String, ?> beforeCreateEdit();

}
