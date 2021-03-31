package processlog.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import processlog.model.EventEntry;

@Repository
public interface EventEntryRepo extends CrudRepository<EventEntry, Long> {
}
