package processlog.data;

import org.springframework.data.repository.CrudRepository;
import processlog.model.EventEntry;

public interface DataRepoHandler {

    enum DBType {HSQLDB}

    CrudRepository<EventEntry, Long> getEventEntryRepo();
}
