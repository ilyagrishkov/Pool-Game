package nl.tudelft.cse.sem.server.storage.database;

import java.util.Properties;
import lombok.NoArgsConstructor;
import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseScore;
import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Class to create and configure hibernate.
 */
@NoArgsConstructor
public class HibernateUtil {
    public static final String MYSQL_USER = "MYSQL_USER";
    public static final String MYSQL_PASS = "MYSQL_PASS";
    public static final String MYSQL_URL = "MYSQL_URL";
    public static final String MYSQL_DATABASE = "MYSQL_DATABASE";

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory factory;

    /**
     * Create a session factory for the duration of the application.
     * Will drop all changes when shutdown.
     */
    //https://github.com/pmd/pmd/issues/1636 Suppress warning as it is removed in future versions due to not working properly
    @SuppressWarnings("PMD")
    public static SessionFactory getSessionFactory(Properties properties) {
        if (factory == null || factory.isClosed()) {
            logger.debug("Creating SessionFactory");

            Configuration configuration = new Configuration();
            configuration.setProperties(properties);
            configuration.addAnnotatedClass(DatabaseUser.class);
            configuration.addAnnotatedClass(DatabaseScore.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(serviceRegistry);
        }
        return factory;
    }

    /**
     * Shuts down the database connection dropping all changes.
     */
    public static void shutdown() {
        factory.close();
    }
}
