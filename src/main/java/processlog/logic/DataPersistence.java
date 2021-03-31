package processlog.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import processlog.data.DataRepoHandler;
import processlog.data.RepoFactory;
import processlog.model.EventEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DataPersistence {

    @Value("${processlog.dbtype:HSQLDB}")
    private String dbtype;

    @Transactional
    public void write(Map<String, EventEntry> map) {
        CrudRepository<EventEntry, Long> eventEntryRepo = RepoFactory.getInvokeStrategy(DataRepoHandler.DBType.valueOf(dbtype)).getEventEntryRepo();
        List<EventEntry> list = new ArrayList<>(map.values());
        eventEntryRepo.saveAll(list);
    }
}
