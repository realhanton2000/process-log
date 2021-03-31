package processlog.stream;

import java.util.HashMap;
import java.util.Map;

public class StreamGenFactory {

    private static Map<StreamGenerator.StreamType, StreamGenerator> strategyMap = new HashMap<StreamGenerator.StreamType, StreamGenerator>();

    public static StreamGenerator getInvokeStrategy(StreamGenerator.StreamType type) {
        return strategyMap.get(type);
    }

    public static void register(StreamGenerator.StreamType type, StreamGenerator streamGen) {
        if (type == null || streamGen == null) {
            return;
        }
        strategyMap.put(type, streamGen);
    }
}
