package processlog.data;

import java.util.HashMap;
import java.util.Map;

public class RepoFactory {

    private static Map<DataRepoHandler.DBType, DataRepoHandler> strategyMap = new HashMap<>();

    public static DataRepoHandler getInvokeStrategy(DataRepoHandler.DBType type) {
        return strategyMap.get(type);
    }

    public static void register(DataRepoHandler.DBType type, DataRepoHandler dataRepoHandler) {
        if (type == null || dataRepoHandler == null) {
            return;
        }
        strategyMap.put(type, dataRepoHandler);
    }
}
