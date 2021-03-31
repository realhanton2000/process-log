package processlog.logic;

import org.junit.*;
import org.junit.rules.ExpectedException;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ProcessFlowJSONExceptionTest.TestConfig.class, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ProcessFlowJSONExceptionTest extends TestTemplate {

    @Autowired
    private ProcessFlow processFlow;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
        Path testFile = Paths.get("src", "test", "resources", "file", "logfile-invalid.txt");
        tempDir = folder.newFolder();
        Path tempFile = Paths.get(tempDir.getAbsolutePath(), "logfile.txt");
        Files.copy(testFile, tempFile);
    }

    @After
    public void after() {
        FileSystemUtils.deleteRecursively(tempDir);
    }

    @Test
    public void testProcessFlow_invalid() {
        //require event id
        exceptionRule.expect(NullPointerException.class);
        processFlow.process(tempDir.getAbsolutePath());
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:application-test.properties")
    @ComponentScan(basePackages = "processlog")
    static class TestConfig {

    }


}
