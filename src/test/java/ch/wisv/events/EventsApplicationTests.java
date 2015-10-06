package ch.wisv.events;

import ch.wisv.events.model.Event;
import ch.wisv.events.model.Person;
import ch.wisv.events.model.Registration;
import ch.wisv.events.repository.EventRepository;
import ch.wisv.events.repository.PersonRepository;
import ch.wisv.events.repository.RegistrationRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EventsApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventsApplicationTests {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    RegistrationRepository registrationRepository;

    private static final Logger log = LoggerFactory.getLogger(EventsApplicationTests.class);

    @Test
    public void contextLoads() {
    }

    @Test
    @Transactional
    @Commit
    /**
     * Create some test data
     */
    public void model1() {
        Event event1 = new Event("Borrel");
        event1.setStart(LocalDateTime.now().plusDays(1));
        event1.setEnd(LocalDateTime.now().plusHours(26));
        event1.setRegistrationStart(LocalDateTime.now().plusHours(1));
        event1.setRegistrationEnd(LocalDateTime.now().plusHours(12));
        event1 = eventRepository.save(event1);
        assertThat(eventRepository.count(), equalTo(1L));
        printObjects("Events", eventRepository.findAll());

        Person person1 = personRepository.save(new Person("derp", "herp@example.com"));
        assertThat(personRepository.count(), equalTo(1L));
        printObjects("People", personRepository.findAll());

        registrationRepository.save(new Registration(person1, event1, LocalDateTime.now(), "0000"));
        assertThat(personRepository.count(), equalTo(1L));
        printObjects("Registrations", registrationRepository.findAll());
    }

    @Test
    @Transactional
    /**
     * Assert that persistence works as expected
     */
    public void model2() {
        assertThat(personRepository.count(), equalTo(1L));
        assertThat(eventRepository.findAll().iterator().next().getRegistrations().size(), equalTo(1));
        assertThat(personRepository.findAll().iterator().next().getRegistrations().size(), equalTo(1));

        assertThat(eventRepository.findByEndAfter(LocalDateTime.now().plusHours(12)).size(), equalTo(1));
        assertThat(eventRepository.findByEndAfter(LocalDateTime.now().plusDays(2)).size(), equalTo(0));

        assertThat(eventRepository.findByRegistrationStartBeforeAndRegistrationEndAfter(LocalDateTime.now().plusHours
                (3), LocalDateTime.now().plusHours(3)).size(), equalTo(1));
        assertThat(eventRepository.findByRegistrationStartBeforeAndRegistrationEndAfter(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)).size(), equalTo(0));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    /**
     * Email should be unique
     */
    public void uniqueness() {
        personRepository.save(new Person("derp", "herp@example.com"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    /**
     * Two registrations for same event cannot have same code
     */
    public void uniqueness2() {
        Event event1 = eventRepository.findAll().iterator().next();
        Person person2 = personRepository.save(new Person("herp", "derp@example.com"));
        registrationRepository.save(new Registration(person2, event1, LocalDateTime.now(), "0000"));
        printObjects("Registrations", registrationRepository.findAll());
    }

    @Test
    @Transactional
    /**
     * Registering for another event with same code should succeed
     */
    public void uniqueness3() {
        Event event2 = eventRepository.save(new Event("Lecture"));
        Person person2 = personRepository.save(new Person("herp", "derp@example.com"));
        registrationRepository.save(new Registration(person2, event2, LocalDateTime.now(), "0000"));
        printObjects("Registrations", registrationRepository.findAll());
    }

    private static void printObjects(String title, Iterable<?> objects) {
        log.info(title + ":");
        StreamSupport.stream(objects.spliterator(), false).map(Object::toString).map(s -> "- " + s).forEach(log::info);
    }

}
