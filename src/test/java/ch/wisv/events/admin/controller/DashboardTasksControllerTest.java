package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.webhook.WebhookTask;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardTasksControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        WebhookTask webhookTask = this.createWebhookTask();
        webhookRepository.saveAndFlush(webhookTask.getWebhook());
        webhookTaskRepository.saveAndFlush(webhookTask);

        mockMvc.perform(get("/administrator/tasks"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/tasks/index"))
                .andExpect(model().attribute("tasks", ImmutableList.of(webhookTask)));
    }

}