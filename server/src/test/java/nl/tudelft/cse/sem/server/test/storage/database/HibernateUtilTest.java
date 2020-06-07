package nl.tudelft.cse.sem.server.test.storage.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Properties;
import nl.tudelft.cse.sem.server.Bootstrap;
import nl.tudelft.cse.sem.server.storage.database.HibernateUtil;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HibernateUtilTest {
    private transient Properties properties;

    @BeforeEach
    public void setUp() {
        this.properties = Bootstrap.getDefaultDatabaseProperties();

        // Get properties for test db
        properties.put(Environment.URL,
            "jdbc:mysql://" + System.getProperty("mysql.location")
                + System.getProperty("mysql.port")
                + "/" + System.getProperty("mysql.db")
                + "?autoReconnect=true&createDatabaseIfNotExist=true");
        properties.put(Environment.PASS, System.getProperty("mysql.pw"));
        properties.put(Environment.USER, System.getProperty("mysql.user"));
    }

    @Test
    void testCreationOfFactory() {
        assertNotNull(HibernateUtil.getSessionFactory(this.properties));

        HibernateUtil.shutdown();

        assertFalse(HibernateUtil.getSessionFactory(this.properties).isClosed());
        assertEquals(HibernateUtil.getSessionFactory(this.properties),
            HibernateUtil.getSessionFactory(this.properties));
    }
}
