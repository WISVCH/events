package ch.wisv.events;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * ControllerTest class.
 */
@Transactional
public abstract class ControllerTest {

    /**
     * Testing utils.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();
}