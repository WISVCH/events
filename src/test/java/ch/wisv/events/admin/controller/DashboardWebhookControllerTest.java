package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.webhook.Webhook;
import com.google.common.collect.ImmutableList;
import static org.hamcrest.Matchers.any;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardWebhookControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/webhooks/index"))
                .andExpect(model().attribute("webhooks", ImmutableList.of(webhook)));
    }

    @Test
    public void testView() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/view/" + webhook.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/webhooks/view"))
                .andExpect(model().attribute("webhook", webhook));
    }

    @Test
    public void testViewNotFound() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/view/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/"))
                .andExpect(flash().attribute("warning", "Webhook with key not-found not found!"));
    }

    @Test
    public void testCreateGet() throws Exception {
        mockMvc.perform(get("/administrator/webhooks/create"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/webhooks/webhook"))
                .andExpect(model().attribute("webhook", any(Webhook.class)));
    }

    @Test
    public void testCreateGetModelAlreadySet() throws Exception {
        Webhook webhook = this.createWebhook();
        mockMvc.perform(
                get("/administrator/webhooks/create").flashAttr("webhook", webhook))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/webhooks/webhook"))
                .andExpect(model().attribute("webhook", webhook));
    }

    @Test
    public void testCreatePostMissingPayload() throws Exception {
        Webhook webhook = new Webhook();

        mockMvc.perform(post("/administrator/webhooks/create").sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/create/"))
                .andExpect(flash().attribute("error", "Payload URL can not be empty!"));
    }

    @Test
    public void testCreatePostMissingLdapGroup() throws Exception {
        Webhook webhook = new Webhook();

        mockMvc.perform(post("/administrator/webhooks/create")
                                .param("payloadUrl", "https://test.frl")
                                .sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/create/"))
                .andExpect(flash().attribute("error", "LDAP group can not be null!"));
    }

    @Test
    public void testCreatePost() throws Exception {
        Webhook webhook = new Webhook();

        mockMvc.perform(post("/administrator/webhooks/create")
                                .param("payloadUrl", "https://test.frl")
                                .param("ldapGroup", "BEHEER")
                                .sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/"))
                .andExpect(flash().attribute("success", "Webhook https://test.frl has been added!"));
    }

    @Test
    public void testEditGet() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/edit/" + webhook.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/webhooks/webhook"))
                .andExpect(model().attribute("webhook", webhook));
    }

    @Test
    public void testEditGetNotFound() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/edit/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/"))
                .andExpect(flash().attribute("warning", "Webhook with key not-found not found!"));
    }

    @Test
    public void testEditPost() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(post("/administrator/webhooks/edit/" + webhook.getKey())
                                .param("payloadUrl", "https://test.frl")
                                .param("ldapGroup", "BEHEER")
                                .sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/view/" + webhook.getKey()))
                .andExpect(flash().attribute("success", "Webhook changes saves!"));
    }

    @Test
    public void testEditPostInvalidPayloadUrl() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(post("/administrator/webhooks/edit/" + webhook.getKey())
                                .param("payloadUrl", "")
                                .param("ldapGroup", "BEHEER")
                                .sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/edit/" + webhook.getKey()))
                .andExpect(flash().attribute("error", "Payload URL can not be empty!"))
                .andExpect(flash().attribute("webhook", any(Webhook.class)));
    }

    @Test
    public void testEditPostInvalidLdapGroup() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(post("/administrator/webhooks/edit/" + webhook.getKey())
                                .param("payloadUrl", "https://test.frl")
                                .sessionAttr("webhook", webhook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/edit/" + webhook.getKey()))
                .andExpect(flash().attribute("error", "LDAP group can not be null!"))
                .andExpect(flash().attribute("webhook", any(Webhook.class)));
    }

    @Test
    public void testDeleteGet() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/delete/" + webhook.getKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/"))
                .andExpect(flash().attribute("success", "Webhook for " + webhook.getPayloadUrl() + " has been deleted!"));
    }

    @Test
    public void testDeleteGetNotFound() throws Exception {
        Webhook webhook = this.createWebhook();
        webhookRepository.saveAndFlush(webhook);

        mockMvc.perform(get("/administrator/webhooks/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/webhooks/"))
                .andExpect(flash().attribute("error", "Webhook with key not-found not found!"));
    }
}