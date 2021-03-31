package processlog.stream;

import processlog.logic.ProLogException;

import java.util.stream.Stream;

public interface StreamGenerator {
    public enum StreamType {FILE}

    Stream<String> getStream(String source) throws ProLogException;
}
