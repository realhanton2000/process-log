package processlog.model;

import org.springframework.lang.NonNull;

import java.util.Objects;

public class Event {
    public enum StateType {
        STARTED, FINISHED
    }

    @NonNull
    private String id;
    @NonNull
    private StateType state;
    @NonNull
    private long timestamp;
    private String type;
    private String host;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public StateType getState() {
        return state;
    }

    public void setState(StateType state) {
        Objects.requireNonNull(state);
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return this.id + ":" + this.state + ":" + this.timestamp;
    }
}
