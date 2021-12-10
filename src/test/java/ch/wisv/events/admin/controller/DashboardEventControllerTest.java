package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventCategory;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.utils.LdapGroup;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardEventControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/administrator/events"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/index"))
                .andExpect(model().attribute("events", ImmutableList.of(event)));
    }

    @Test
    public void testView() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/administrator/events/view/" + event.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/view"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    public void testViewNotFound() throws Exception {
        mockMvc.perform(get("/administrator/events/view/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("error", "Event with key not-found not found!"));
    }

    @Test
    public void testCreateGet() throws Exception {
        mockMvc.perform(get("/administrator/events/create/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/event"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    public void testCreateGetAlreadySet() throws Exception {
        Event event = this.createEvent();
        mockMvc.perform(get("/administrator/events/create/").flashAttr("event", event))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/event"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    public void testCreatePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("success", "Event Events has been created!"));
    }

    @Test
    public void testCreatePostMissingTitle() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Title is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingShortDescription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Short description is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingDescription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Description is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingStart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Starting time is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingEnd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Ending time is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingTarget() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Target is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingMaxSoldLowerThenTarget() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "80")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Limit should be greater or equal to the target!"));
    }

    @Test
    public void testCreatePostMissingEndBeforeStart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/create/")
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T09:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/create/"))
                .andExpect(flash().attribute("error", "Starting time should be before the ending time"));
    }

    @Test
    public void testEditGet() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/administrator/events/edit/" + event.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/event"))
                .andExpect(model().attribute("event", event));
    }

    @Test
    public void testEditGetNotFound() throws Exception {
        mockMvc.perform(get("/administrator/events/edit/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("error", "Event with key not-found not found!"));
    }

    @Test
    public void testEditPost() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Symposium")
                                .param("shortDescription", "Short description of Symposium")
                                .param("description", "Description of Symposium")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/view/" + event.getKey()))
                .andExpect(flash().attribute("success", "Event changes saved!"));

        Event test = eventService.getByKey(event.getKey());
        assertEquals("Symposium", test.getTitle());
        assertEquals("Short description of Symposium", test.getShortDescription());
        assertEquals("Description of Symposium", test.getDescription());
        assertEquals(LocalDateTime.of(2018,1,1,10,0), test.getStart());
        assertEquals(LocalDateTime.of(2018,1,1,11,0), test.getEnding());
        assertEquals(Integer.valueOf(100), test.getMaxSold());
        assertEquals(Integer.valueOf(100), test.getTarget());
    }

    @Test
    public void testEditPostMissingTitle() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "")
                                .param("shortDescription", "Short description of Symposium")
                                .param("description", "Description of Symposium")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Title is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingShortDescription() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Short description is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingDescription() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Description is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingStart() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Starting time is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingEnd() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("target", "100")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Ending time is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingTarget() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("maxSold", "100")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Target is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingMaxSoldLowerThenTarget() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/administrator/events/edit/" + event.getKey())
                                .file(new MockMultipartFile("file", "orig", null, new byte[0]))
                                .param("title", "Events")
                                .param("shortDescription", "Short description")
                                .param("description", "Description")
                                .param("start", "2018-01-01T10:00")
                                .param("ending", "2018-01-01T11:00")
                                .param("target", "100")
                                .param("maxSold", "80")
                                .sessionAttr("event", new Event()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/edit/" + event.getKey()))
                .andExpect(flash().attribute("error", "Limit should be greater or equal to the target!"));
    }

    @Test
    public void testOverview() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/administrator/events/overview/" + event.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/events/overview"))
                .andExpect(model().attribute("event", event))
                .andExpect(model().attributeExists("tickets"));
    }

    @Test
    public void testOverviewNotFound() throws Exception {
        mockMvc.perform(get("/administrator/events/overview/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("error", "Event with key not-found not found!"));
    }

    @Test
    public void testDelete() throws Exception {
        Event event = this.createEvent();

        mockMvc.perform(get("/administrator/events/delete/" + event.getKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("success", "Event " + event.getTitle() + " has been deleted!"));
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        mockMvc.perform(get("/administrator/events/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/events/"))
                .andExpect(flash().attribute("error", "Event with key not-found not found!"));
    }
}