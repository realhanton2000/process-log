package processlog.data;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import processlog.model.EventEntry;
import processlog.repo.EventEntryRepo;

@Component
public class HSQLDBRepoHandler implements DataRepoHandler, InitializingBean {
    @Autowired
    private EventEntryRepo eventEntryRepo;

    @Override
    public CrudRepository<EventEntry, Long> getEventEntryRepo() {
        return eventEntryRepo;
    }

    @Override
    public void afterPropertiesSet() {
        RepoFactory.register(DataRepoHandler.DBType.HSQLDB, this);
    }
}
