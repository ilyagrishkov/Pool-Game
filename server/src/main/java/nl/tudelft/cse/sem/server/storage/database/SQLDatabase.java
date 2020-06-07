package nl.tudelft.cse.sem.server.storage.database;

import java.util.Properties;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.entity.store.impl.SQLScoreStore;
import nl.tudelft.cse.sem.server.entity.store.impl.SQLUserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An implementation of the {@link Database} interface which uses a SQL database to store the data.
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLDatabase implements Database {
    private static final Logger logger = LogManager.getLogger(SQLDatabase.class);

    private transient UserStore userStore;
    private transient ScoreStore scoreStore;

    /**
     * Constructor.
     */
    public SQLDatabase() {
        this.userStore = new SQLUserStore();
        this.scoreStore = new SQLScoreStore();
    }

    @Override
    public void init(@NonNull Properties properties) {
        logger.info("Starting user store");
        this.userStore.init(properties);

        logger.info("Starting score store");
        this.scoreStore.init(properties, this.userStore);

        logger.info("Started database");
    }

    @Override
    public void teardown() {
        logger.info("Stopping the user store");
        this.userStore.teardown();

        logger.info("Stopping the score store");
        this.scoreStore.teardown();

        logger.info("Stopped database");
    }

    @Override
    public UserStore getUserStore() {
        return this.userStore;
    }

    @Override
    public ScoreStore getScoreStore() {
        return this.scoreStore;
    }
}
