package processlog.logic;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import processlog.model.Event;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EventPoolTest.TestConfig.class, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class EventPoolTest {

    @Autowired
    private EventPool pool;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void testExceptionOne() throws ProLogException {
        exceptionRule.expect(ProLogException.class);
        exceptionRule.expectMessage("The writing handler must be set before pushing event.");
        pool.put(new Event());
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:application-test.properties")
    @ComponentScan(basePackages = "processlog")
    static class TestConfig {


    }
}
