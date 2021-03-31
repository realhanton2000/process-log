package processlog.model;

import java.util.Objects;

public class EventPair {

    private String id;
    private Event startedEvent;
    private Event finishedEvent;

    public EventPair(String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public Event getStartedEvent() {
        return startedEvent;
    }

    public void setStartedEvent(Event startedEvent) {
        this.startedEvent = startedEvent;
    }

    public Event getFinishedEvent() {
        return finishedEvent;
    }

    public void setFinishedEvent(Event finishedEvent) {
        this.finishedEvent = finishedEvent;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventPair) {
            EventPair other = (EventPair) obj;
            return this.id.equals(other.id);
        }
        return false;
    }
}
