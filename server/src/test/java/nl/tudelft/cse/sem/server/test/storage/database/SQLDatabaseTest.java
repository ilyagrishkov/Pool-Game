package nl.tudelft.cse.sem.server.test.storage.database;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.testing.NullPointerTester;
import java.util.Properties;
import nl.tudelft.cse.sem.server.Bootstrap;
import nl.tudelft.cse.sem.server.storage.database.SQLDatabase;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLDatabaseTest {
    private transient Properties properties;
    private transient SQLDatabase database;

    /**
     * Sets up dependencies and database.
     */
    @BeforeEach
    public void setUp() {
        this.database = new SQLDatabase();
        this.properties = Bootstrap.getDefaultDatabaseProperties();

        // Get properties for test db
        properties.put(Environment.URL,
            "jdbc:mysql://" + System.getProperty("mysql.location")
                + System.getProperty("mysql.port")
                + "/" + System.getProperty("mysql.db")
                + "?autoReconnect=true&createDatabaseIfNotExist=true");
        properties.put(Environment.PASS, System.getProperty("mysql.pw"));
        properties.put(Environment.USER, System.getProperty("mysql.user"));

        this.database.init(this.properties);
    }

    @AfterEach
    public void tearDown() {
        this.database.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(SQLDatabase.class);
        tester.testAllPublicInstanceMethods(this.database);
    }

    @Test
    public void shouldReturnUserStore() {
        assertNotNull(this.database.getUserStore());
    }

    @Test
    public void shouldReturnScoreStore() {
        assertNotNull(this.database.getScoreStore());
    }
}
