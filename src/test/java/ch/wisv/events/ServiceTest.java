package ch.wisv.events;

import javax.transaction.Transactional;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Transactional
public abstract class ServiceTest {

    /**
     * ExpectedException Object
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

}
