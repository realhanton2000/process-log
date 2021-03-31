package processlog.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "event")
public class EventEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    private String eventId;

    @NonNull
    private long duration;

    private String type;

    private String host;

    @NonNull
    private boolean alert;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
