package com.test.margo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.margo.helper.EventHelper;
import org.junit.*;

import java.io.IOException;
import java.sql.*;

import static junit.framework.TestCase.assertEquals;

public class MargoApplicationIT {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS EVENT (ID VARCHAR(20), DURATION INTEGER, TYPE VARCHAR(50), HOST VARCHAR(50), ALERT BOOLEAN)";
    private ObjectMapper objectMapper;
    private EventHelper eventHelper;
    private MargoApplication applicationRunner;

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
            connection.commit();
        }
    }

    @AfterClass
    public static void destroy() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE EVENT");
            connection.commit();
            statement.executeUpdate("SHUTDOWN");
            connection.commit();
        }
    }

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        eventHelper = new EventHelper();

    }

    @After
    public void clean() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM EVENT");
            connection.commit();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:eventdbTest;ifexists=false", "user", "");
    }

    @Test
    public void testRunProcessesTestFileAndSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/testFile"};
        Connection connection = getConnection();

        applicationRunner = new MargoApplication(objectMapper, connection, eventHelper);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM event");

            assertEquals("Should save 3 records", 3, getSize(resultSet));

            ResultSet result1 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgra' AND DURATION=5 AND TYPE='APPLICATION_LOG' AND HOST='12345' AND ALERT=TRUE");
            assertEquals("Should have 1 record", 1, getSize(result1));

            ResultSet result2 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgrb' AND DURATION=3 AND ALERT=FALSE");
            assertEquals("Should have 1 record", 1, getSize(result2));

            ResultSet result3 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgrc' AND DURATION=8 AND ALERT=TRUE");
            assertEquals("Should save 1 record", 1, getSize(result3));
        }
    }

    @Test
    public void testRunDoesntProcessInvalidJsonAndDoesntSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/testFile_with_invalid_json"};
        Connection connection = getConnection();

        applicationRunner = new MargoApplication(objectMapper, connection, eventHelper);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT");

            assertEquals("Should save 0 records", 0, getSize(resultSet));
        }
    }

    @Test
    public void testRunProcessJsonWithAdditionalFieldsAndSavesResultsToDatabase() throws IOException, SQLException {
        String[] args = {"src/test/resources/testFile_with_extra_fields"};
        Connection connection = getConnection();

        applicationRunner = new MargoApplication(objectMapper, connection, eventHelper);
        applicationRunner.run(args);

        try (Connection assertConnection = getConnection(); Statement statement = assertConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT");

            assertEquals("Should save 3 records", 3, getSize(resultSet));

            ResultSet result1 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgra' AND DURATION=5 AND TYPE='APPLICATION_LOG' AND HOST='12345' AND ALERT=TRUE");
            assertEquals("Should have 1 record", 1, getSize(result1));

            ResultSet result2 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgrb' AND DURATION=3 AND ALERT=FALSE");
            assertEquals("Should have 1 record", 1, getSize(result2));

            ResultSet result3 = statement.executeQuery("SELECT COUNT(*) AS TOTAL FROM EVENT WHERE ID='scsmbstgrc' AND DURATION=8 AND ALERT=TRUE");
            assertEquals("Should save 1 record", 1, getSize(result3));
        }
    }

    private int getSize(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt("total");
    }
}
