package processlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import processlog.logic.ProcessFlow;

@SpringBootApplication
@EnableJpaRepositories("processlog.repo")
@EntityScan("processlog.model")
@EnableTransactionManagement
public class Application implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ProcessFlow flow;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).headless(true).run(args);
    }

    @Override
    public void run(String... args) {
        if (args.length == 1) {
            flow.process(args[0]);
        } else {
            logger.error("App takes file path as only one argument.");
        }
    }
}
