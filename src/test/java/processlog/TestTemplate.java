package processlog;

import processlog.model.EventEntry;

import java.sql.*;

public class TestTemplate {

    protected static final String LOGGER_NAME = "processlog";

    private final static String CREATE_EVENT_TABLE =
            "CREATE TABLE event " +
                    "(id BIGINT NOT NULL, alert BOOLEAN NOT NULL, duration BIGINT NOT NULL, " +
                    "event_id VARCHAR(255) NOT NULL, host VARCHAR(255), type VARCHAR(255), PRIMARY KEY (id))";
    private final static String DROP_EVENT_TABLE =
            "DROP TABLE event";

    public static void initDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_EVENT_TABLE);
            connection.commit();
        }
    }

    public static void destoryDatabase() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_EVENT_TABLE);
            connection.commit();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "user", "password");
    }

    private static final String EVENT_COUNT =
            "SELECT count(*) AS rowcount FROM event";

    protected int getEventCount() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            ResultSet result = statement.executeQuery(EVENT_COUNT);
            result.next();
            return result.getInt("rowcount");
        }
    }

    private static final String EVENT_QUERY =
            "SELECT * FROM event WHERE event_id = ?";

    protected EventEntry getEvent(String eventId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(EVENT_QUERY)) {
            ps.setString(1, eventId);
            ResultSet result = ps.executeQuery();
            result.next();

            EventEntry ee = new EventEntry();
            ee.setId(result.getLong("id"));
            ee.setEventId(result.getString("event_id"));
            ee.setDuration(result.getLong("duration"));
            ee.setType(result.getString("type"));
            ee.setHost(result.getString("host"));
            ee.setAlert(result.getBoolean("alert"));
            return ee;
        }
    }

}
