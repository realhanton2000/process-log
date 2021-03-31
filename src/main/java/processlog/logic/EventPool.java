package processlog.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import processlog.model.Event;
import processlog.model.EventPair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class EventPool {

    private Logger logger = LoggerFactory.getLogger(EventPool.class);

    @Value("${processlog.writeTrigger:100}")
    private int writeTrigger;
    @Value("${processlog.maxSize:-1}")
    private int maxSize;

    private volatile Map<String, EventPair> all = new HashMap<>();
    private volatile Map<String, EventPair> paired = new HashMap<>();

    private Consumer<Map<String, EventPair>> writingHandler;

    public synchronized void put(Event event) throws ProLogException {
        if (writingHandler == null) {
            throw new ProLogException("The writing handler must be set before pushing event.");
        }
        EventPair ep;
        if ((ep = all.get(event.getId())) == null) {
            ep = new EventPair(event.getId());
            switch (event.getState()) {
                case STARTED:
                    ep.setStartedEvent(event);
                    break;
                case FINISHED:
                default:
                    ep.setFinishedEvent(event);
            }
            all.put(event.getId(), ep);
        } else {
            switch (event.getState()) {
                case STARTED:
                    if (ep.getStartedEvent() != null) {
                        throw new ProLogException("Duplicated Started event:" + event.toString());
                    } else {
                        ep.setStartedEvent(event);
                    }
                    break;
                case FINISHED:
                default:
                    if (ep.getFinishedEvent() != null) {
                        throw new ProLogException("Duplicated Finished event:" + event.toString());
                    } else {
                        ep.setFinishedEvent(event);
                    }
            }
            if (ep.getStartedEvent() != null && ep.getFinishedEvent() != null) {
                paired.put(ep.getId(), ep);
                all.remove(ep.getId());
            }
        }
        if (paired.size() >= writeTrigger) {
            writeAndFlushPaired();
        }
        //disable size check when maxSize is not positive value
        if (maxSize > 0 && all.size() >= maxSize) {
            writeAndFlushPaired();
            all.clear();
            throw new ProLogException("Reached max allowed limitation of unpaired events.");
        }
    }

    public synchronized void flush() {
        writeAndFlushPaired();
        all.clear();
    }

    protected void writeAndFlushPaired() {
        logger.debug("Going to write [" + paired.size() + "] events into persistence layer.");
        writingHandler.accept(paired);
        paired.clear();
    }

    public void setWritingHandler(Consumer<Map<String, EventPair>> writingHandler) {
        this.writingHandler = writingHandler;
    }

}
