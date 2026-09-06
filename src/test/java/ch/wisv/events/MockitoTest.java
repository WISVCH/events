package ch.wisv.events;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Base class for JUnit 4 unit tests that use Mockito.
 */
public abstract class MockitoTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();
}
