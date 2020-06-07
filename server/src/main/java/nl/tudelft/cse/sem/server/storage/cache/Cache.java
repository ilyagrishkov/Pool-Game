package nl.tudelft.cse.sem.server.storage.cache;

import java.util.Properties;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.jwt.store.RefreshTokenStore;

/**
 * A wrapper for the separate cache interfaces.
 */
public interface Cache {
    /**
     * Initialized the cache.
     *
     * @param properties {@link Properties} to use for initialization
     */
    void init(@NonNull Properties properties);

    /**
     * Stops the cache.
     */
    void teardown();

    /**
     * Returns the refresh token store.
     *
     * @return A {@link RefreshTokenStore}
     */
    RefreshTokenStore getTokenStore();
}
