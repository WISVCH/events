package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.WebhookInvalidException;
import ch.wisv.events.core.exception.normal.WebhookNotFoundException;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.service.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrator/webhooks")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardWebhookController {

    /** WebhookService. */
    private final WebhookService webhookService;

    /**
     * DashboardWebhookController constructor.
     *
     * @param webhookService of type WebhookService.
     */
    @Autowired
    public DashboardWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("webhooks", webhookService.getAll());

        return "admin/webhooks/index";
    }

    /**
     * Edit existing vendor [GET "/edit/{key}"].
     *
     * @param model Spring model
     * @param key   key of the vendor
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping("/view/{key}")
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Webhook webhook = webhookService.getByKey(key);
            model.addAttribute("webhook", webhook);

            return "admin/webhooks/view";
        } catch (WebhookNotFoundException e) {
            redirect.addFlashAttribute("warning", e.getMessage());

            return "redirect:/administrator/webhooks/";
        }
    }

    /**
     * Create a new vendor [GET "/create/"].
     *
     * @param model Spring model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("webhook")) {
            model.addAttribute("webhook", new Webhook());
        }

        return "admin/webhooks/webhook";
    }

    /**
     * Add a new Webhook.
     *
     * @param redirect RedirectAttributes
     * @param webhook  Webhook with the needed information
     *
     * @return redirect to other page
     */
    @PostMapping("/create")
    public String create(RedirectAttributes redirect, @ModelAttribute Webhook webhook) {
        try {
            webhookService.create(webhook);
            redirect.addFlashAttribute("success", "Webhook " + webhook.getPayloadUrl() + " has been added!");

            return "redirect:/administrator/webhooks/";
        } catch (WebhookInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("webhook", webhook);

            return "redirect:/administrator/webhooks/create/";
        }
    }

    /**
     * Edit existing vendor [GET "/edit/{key}"].
     *
     * @param model Spring model
     * @param key   key of the vendor
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping("/edit/{key}")
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Webhook webhook = webhookService.getByKey(key);
            model.addAttribute("webhook", webhook);

            return "admin/webhooks/webhook";
        } catch (WebhookNotFoundException e) {
            redirect.addFlashAttribute("waring", e.getMessage());

            return "redirect:/administrator/webhooks/";
        }
    }

    /**
     * Update a existing Webhook.
     *
     * @param redirect RedirectAttributes
     * @param webhook  Webhook webhook with the needed information
     *
     * @return redirect to edit page
     */
    @PostMapping("/edit/{key}")
    public String edit(RedirectAttributes redirect, @ModelAttribute Webhook webhook, @PathVariable String key) {
        try {
            webhook.setKey(key);
            webhookService.update(webhook);
            redirect.addFlashAttribute("success", "Webhook changes saves!");

            return "redirect:/administrator/webhooks/view/" + webhook.getKey() + "/";
        } catch (WebhookInvalidException | WebhookNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("webhook", webhook);

            return "redirect:/administrator/webhooks/edit/" + webhook.getKey() + "/";
        }
    }

    /**
     * Update a existing vendor.
     *
     * @param redirect RedirectAttributes
     * @param key      Vendor model with the needed information
     *
     * @return redirect to edit page
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Webhook webhook = webhookService.getByKey(key);
            webhookService.delete(webhook);

            redirect.addFlashAttribute("success", "Webhook for " + webhook.getPayloadUrl() + " has been deleted!");

            return "redirect:/administrator/webhooks/";
        } catch (WebhookNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/administrator/webhooks/";
        }
    }
}
