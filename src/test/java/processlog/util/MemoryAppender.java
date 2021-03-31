package processlog.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.List;
import java.util.stream.Collectors;

public class MemoryAppender extends ListAppender<ILoggingEvent> {
    public void reset() {
        this.list.clear();
    }

    public boolean contains(String string, Level level) {
        return this.list.stream()
                .anyMatch(event -> event.getMessage().contains(string)
                        && event.getLevel().equals(level));
    }

    public List<ILoggingEvent> search(String string, Level level) {
        return this.list.stream()
                .filter(event -> event.getMessage().contains(string)
                        && event.getLevel().equals(level))
                .collect(Collectors.toList());
    }

}
