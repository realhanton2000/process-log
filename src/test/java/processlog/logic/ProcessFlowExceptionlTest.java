package processlog.logic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.FileSystemUtils;
import processlog.TestTemplate;
import processlog.util.MemoryAppender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessFlowExceptionlTest.TestConfig.class, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ProcessFlowExceptionlTest extends TestTemplate {

    @Autowired
    private ProcessFlow processFlow;

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

    private MemoryAppender memoryAppender;

    @Before
    public void before() throws IOException {
        Path testFile = Paths.get("src", "test", "resources", "file", "logfile-duplicated.txt");
        tempDir = folder.newFolder();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "logfile.txt");
        Files.copy(testFile, tempFile);

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @After
    public void after() {
        FileSystemUtils.deleteRecursively(tempDir);
        memoryAppender.reset();
    }

    @Test
    public void testProcessFlow_duplicatedEvent() {
        processFlow.process(tempDir.getAbsolutePath());
        List<ILoggingEvent> list = memoryAppender.search("Duplicated Started event", Level.ERROR);
        Assert.assertEquals("Duplicated Event occurred 1 time", 1, list.size());
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:application-test.properties")
    @ComponentScan(basePackages = "processlog")
    static class TestConfig {

    }


}
