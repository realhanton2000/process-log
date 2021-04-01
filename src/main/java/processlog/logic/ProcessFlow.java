package processlog.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import processlog.model.Event;
import processlog.model.EventEntry;
import processlog.model.EventPair;
import processlog.stream.StreamGenFactory;
import processlog.stream.StreamGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class ProcessFlow {
    private Logger logger = LoggerFactory.getLogger(ProcessFlow.class);

    @Autowired
    private EventPool pool;

    @Value("${processlog.streamtype:FILE}")
    private String streamType;

    @Value("${processlog.alertThreshold:4}")
    private long alertThreshold;

    @Autowired
    private DataPersistence dataPersistence;

    public void process(String source) {
        StreamGenerator streamGen = StreamGenFactory.getInvokeStrategy(StreamGenerator.StreamType.valueOf(streamType));
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Prepare and set writingHandler");
        Consumer<Map<String, EventPair>> writingHandler = (paired) -> {
            Map<String, EventEntry> map = convert(paired);
            dataPersistence.write(map);
        };
        pool.setWritingHandler(writingHandler);
        logger.info("Process stream");
        try (Stream<String> lines = streamGen.getStream(source)) {
            Phaser phaser = new Phaser(1);

            Consumer<String> consumer = (line) -> {
                try {
                    if (StringUtils.isEmpty(line)) {
                        throw new ProLogException("This is an empty line.");
                    }
                    Event event = objectMapper.readValue(line, Event.class);

                    phaser.register();
                    new Thread(() -> {
                        try {
                            pool.put(event);
                        } catch (ProLogException e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    }).start();
                } catch (JsonProcessingException e) {
                    logger.error("Exception occurred during JSON deserialization", e);
                } catch (ProLogException e) {
                    logger.error(e.getMessage(), e);
                }
            };
            lines.forEach(consumer);

            phaser.arriveAndAwaitAdvance();
            pool.flush();
        } catch (ProLogException e) {
            logger.error("Exception occurred when getting stream:", e);
        }
    }

    private Map<String, EventEntry> convert(Map<String, EventPair> pairs) {
        Map<String, EventEntry> map = new HashMap<>();
        pairs.values().forEach((ep) -> {
            EventEntry eventEntry = new EventEntry();
            eventEntry.setEventId(ep.getId());
            long duration = ep.getFinishedEvent().getTimestamp() - ep.getStartedEvent().getTimestamp();
            if (duration < 0) {
                logger.info("This event [" + ep.getId() + "] duration is a negative value.");
            }
            eventEntry.setDuration(duration);
            eventEntry.setAlert(duration >= alertThreshold);
            eventEntry.setType((!StringUtils.isEmpty(ep.getStartedEvent().getType())) ?
                    ep.getStartedEvent().getType() : ep.getFinishedEvent().getType());
            eventEntry.setHost((!StringUtils.isEmpty(ep.getStartedEvent().getHost())) ?
                    ep.getStartedEvent().getHost() : ep.getFinishedEvent().getHost());
            map.put(ep.getId(), eventEntry);
        });
        return map;
    }

}
