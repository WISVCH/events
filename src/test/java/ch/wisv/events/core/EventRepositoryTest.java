package ch.wisv.events.core;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventCategory;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.utils.LdapGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.flyway.enabled=false")
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testFindAllSalesVisibleEventsIncludesPublishedStatus() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event event = createEvent(
                "Published event",
                startOfToday.plusHours(1),
                startOfToday.plusHours(2),
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        eventRepository.saveAndFlush(event);

        List<Event> result = findSalesVisibleEvents(startOfToday);

        assertEquals(1, result.size());
        assertEquals("Published event", result.get(0).getTitle());
    }

    @Test
    public void testFindAllSalesVisibleEventsIncludesNotPublishedStatus() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event event = createEvent(
                "Not published event",
                startOfToday.plusHours(1),
                startOfToday.plusHours(2),
                EventStatus.NOT_PUBLISHED,
                LdapGroup.WIFI
        );
        eventRepository.saveAndFlush(event);

        List<Event> result = findSalesVisibleEvents(startOfToday);

        assertEquals(1, result.size());
        assertEquals("Not published event", result.get(0).getTitle());
    }

    @Test
    public void testFindAllSalesVisibleEventsExcludesConceptStatus() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event event = createEvent(
                "Concept event",
                startOfToday.plusHours(1),
                startOfToday.plusHours(2),
                EventStatus.CONCEPT,
                LdapGroup.WIFI
        );
        eventRepository.saveAndFlush(event);

        List<Event> result = findSalesVisibleEvents(startOfToday);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindAllSalesVisibleEventsRespectsEndingBoundaryAtStartOfDay() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event includedBoundary = createEvent(
                "Boundary included",
                startOfToday.plusHours(1),
                startOfToday,
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        Event excludedBeforeBoundary = createEvent(
                "Boundary excluded",
                startOfToday.plusHours(2),
                startOfToday.minusSeconds(1),
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        eventRepository.saveAllAndFlush(List.of(includedBoundary, excludedBeforeBoundary));

        List<Event> result = findSalesVisibleEvents(startOfToday);

        assertEquals(1, result.size());
        assertEquals("Boundary included", result.get(0).getTitle());
    }

    @Test
    public void testFindAllSalesVisibleEventsOrdersByStartAscending() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event later = createEvent(
                "Later",
                startOfToday.plusHours(3),
                startOfToday.plusDays(1),
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        Event earlier = createEvent(
                "Earlier",
                startOfToday.plusHours(1),
                startOfToday.plusDays(1),
                EventStatus.NOT_PUBLISHED,
                LdapGroup.BT
        );
        eventRepository.saveAllAndFlush(List.of(later, earlier));

        List<Event> result = findSalesVisibleEvents(startOfToday);

        assertEquals(2, result.size());
        assertEquals("Earlier", result.get(0).getTitle());
        assertEquals("Later", result.get(1).getTitle());
    }

    @Test
    public void testFindAllSalesVisibleEventsByOrganizedByInIncludesOnlyConfiguredGroups() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event included = createEvent(
                "Included wifi",
                startOfToday.plusHours(2),
                startOfToday.plusDays(1),
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        Event excludedOtherGroup = createEvent(
                "Excluded other group",
                startOfToday.plusHours(3),
                startOfToday.plusDays(1),
                EventStatus.PUBLISHED,
                LdapGroup.BT
        );
        eventRepository.saveAllAndFlush(List.of(included, excludedOtherGroup));

        List<Event> result = findSalesVisibleEventsForGroups(startOfToday, List.of(LdapGroup.WIFI));

        assertEquals(1, result.size());
        assertEquals("Included wifi", result.get(0).getTitle());
    }

    @Test
    public void testFindAllSalesVisibleEventsByOrganizedByInStillAppliesStatusAndEndingFilters() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        Event excludedConcept = createEvent(
                "Excluded concept wifi",
                startOfToday.plusHours(1),
                startOfToday.plusDays(1),
                EventStatus.CONCEPT,
                LdapGroup.WIFI
        );
        Event excludedOld = createEvent(
                "Excluded old wifi",
                startOfToday.plusHours(2),
                startOfToday.minusSeconds(1),
                EventStatus.PUBLISHED,
                LdapGroup.WIFI
        );
        eventRepository.saveAllAndFlush(List.of(excludedConcept, excludedOld));

        List<Event> result = findSalesVisibleEventsForGroups(startOfToday, List.of(LdapGroup.WIFI));

        assertFalse(result.stream().anyMatch(event -> event.getTitle().equals("Excluded concept wifi")));
        assertFalse(result.stream().anyMatch(event -> event.getTitle().equals("Excluded old wifi")));
        assertTrue(result.isEmpty());
    }

    private List<Event> findSalesVisibleEvents(LocalDateTime from) {
        return eventRepository.findAllSalesVisibleEvents(
                from,
                List.of(EventStatus.PUBLISHED, EventStatus.NOT_PUBLISHED)
        );
    }

    private List<Event> findSalesVisibleEventsForGroups(LocalDateTime from, List<LdapGroup> groups) {
        return eventRepository.findAllSalesVisibleEventsByOrganizedByIn(
                from,
                List.of(EventStatus.PUBLISHED, EventStatus.NOT_PUBLISHED),
                groups
        );
    }

    private Event createEvent(
            String title,
            LocalDateTime start,
            LocalDateTime ending,
            EventStatus status,
            LdapGroup organizedBy
    ) {
        Event event = new Event();
        event.setTitle(title);
        event.setShortDescription("Short " + title);
        event.setDescription("Description " + title);
        event.setLocation("Delft");
        event.setStart(start);
        event.setEnding(ending);
        event.setTarget(10);
        event.setPublished(status);
        event.setOrganizedBy(organizedBy);
        event.setCategories(List.of(EventCategory.SOCIAL));
        return event;
    }
}
