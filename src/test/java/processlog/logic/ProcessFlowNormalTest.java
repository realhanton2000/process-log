package processlog.logic;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
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
import org.springframework.util.FileSystemUtils;
import processlog.TestTemplate;
import processlog.model.EventEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessFlowNormalTest.TestConfig.class, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ProcessFlowNormalTest extends TestTemplate {

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
        //scsmbstgra
        EventEntry scsmbstgra = getEvent("scsmbstgra");
        Assert.assertTrue("scsmbstgra shall has alert", scsmbstgra.isAlert());
        Assert.assertEquals("scsmbstgra duration shall be 5", 5, scsmbstgra.getDuration());
        Assert.assertEquals("scsmbstgra host shall be 12345", "12345", scsmbstgra.getHost());
        Assert.assertEquals("scsmbstgra type shall be APPLICATION_LOG", "APPLICATION_LOG", scsmbstgra.getType());
        //scsmbstgrb
        EventEntry scsmbstgrb = getEvent("scsmbstgrb");
        Assert.assertFalse("scsmbstgrb shall not has alert", scsmbstgrb.isAlert());
        Assert.assertEquals("scsmbstgrb duration shall be 3", 3, scsmbstgrb.getDuration());
        Assert.assertNull("scsmbstgrb host shall be null", scsmbstgrb.getHost());
        Assert.assertNull("scsmbstgrb type shall be null", scsmbstgrb.getType());
        //scsmbstgrc
        EventEntry scsmbstgrc = getEvent("scsmbstgrc");
        Assert.assertTrue("scsmbstgrc shall has alert", scsmbstgrc.isAlert());
        Assert.assertEquals("scsmbstgrc duration shall be 8", 8, scsmbstgrc.getDuration());
        Assert.assertNull("scsmbstgrc host shall be null", scsmbstgrc.getHost());
        Assert.assertNull("scsmbstgrc type shall be null", scsmbstgrc.getType());
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:application-test.properties")
    @ComponentScan(basePackages = "processlog")
    static class TestConfig {

    }
}
