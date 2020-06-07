package nl.tudelft.cse.sem.server.storage.database;

import java.util.Properties;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.entity.store.UserStore;

/**
 * A wrapper for the separate database interfaces.
 */
public interface Database {
    /**
     * Initialized the database.
     *
     * @param properties {@link Properties} to use for initialization
     */
    void init(@NonNull Properties properties);

    /**
     * Stops the database.
     */
    void teardown();

    /**
     * Returns the user store.
     *
     * @return A {@link UserStore}
     */
    UserStore getUserStore();

    /**
     * Returns the score store.
     *
     * @return A {@link ScoreStore}
     */
    ScoreStore getScoreStore();
}
