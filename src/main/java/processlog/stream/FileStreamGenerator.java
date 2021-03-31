package processlog.stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import processlog.logic.ProLogException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

@Component
public class FileStreamGenerator implements StreamGenerator, InitializingBean {

    private static final String FILENAME = "logfile.txt";

    @Override
    public Stream<String> getStream(String source) throws ProLogException {
        File dir = new File(source);
        if (dir.isDirectory()) {
            File file = new File(dir.getAbsolutePath() + File.separator + FILENAME);
            if (file.exists() && file.isFile()) {
                try {
                    return Files.lines(file.toPath());
                } catch (IOException ioe) {
                    throw new ProLogException("IOException occurred when reading file", ioe);
                }
            }
        }
        throw new ProLogException("Unable to locate " + FILENAME + " under " + source);
    }

    @Override
    public void afterPropertiesSet() {
        StreamGenFactory.register(StreamType.FILE, this);
    }
}
