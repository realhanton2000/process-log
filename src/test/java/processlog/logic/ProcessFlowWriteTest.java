package processlog.logic;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.FileSystemUtils;
import processlog.TestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessFlowWriteTest.TestConfig.class,
        initializers = ProcessFlowWriteTest.PropertyOverrideContextInitializer.class,
        loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ProcessFlowWriteTest extends TestTemplate {

    @Autowired
    private ProcessFlow processFlow;

    @Autowired
    private EventPool testPool;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws SQLException, ClassNotFoundException {
        TestTemplate.initDatabase();
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        TestTemplate.destoryDatabase();
    }

    private File tempDir;

    @Before
    public void before() throws IOException {
        Path testFile = Paths.get("src", "test", "resources", "file", "logfile-normal.txt");
        tempDir = folder.newFolder();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "logfile.txt");
        Files.copy(testFile, tempFile);
    }

    @After
    public void after() {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    public void testProcessFlow() throws SQLException {
        processFlow.process(tempDir.getAbsolutePath());
        Assert.assertEquals("Row count must be 3", 3, getEventCount());
        if (testPool instanceof TestEventPool) {
            Assert.assertEquals("Write shall occur 4 times", 4, ((TestEventPool) testPool).numOfWrite);
        }
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:application-test.properties")
    @ComponentScan(basePackages = "processlog")
    public static class TestConfig {
        @Bean
        public EventPool eventPool() {
            return new TestEventPool();
        }
    }

    public static class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final int WRITE_TRIGGER = 1;

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext, "processlog.writeTrigger=" + WRITE_TRIGGER);
        }
    }

    public static class TestEventPool extends EventPool {

        private int numOfWrite;

        @Override
        protected void writeAndFlushPaired() {
            super.writeAndFlushPaired();
            numOfWrite++;
        }

    }
}
