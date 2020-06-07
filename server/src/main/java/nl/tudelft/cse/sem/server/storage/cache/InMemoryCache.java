package nl.tudelft.cse.sem.server.storage.cache;

import java.util.Properties;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.jwt.store.InMemoryTokenStore;
import nl.tudelft.cse.sem.server.jwt.store.RefreshTokenStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An implementation of the {@link Cache} interface which uses several in-memory cached
 * to store the data.
 */
public class InMemoryCache implements Cache {
    private static final Logger logger = LogManager.getLogger(InMemoryCache.class);

    private transient RefreshTokenStore refreshTokenStore;

    /**
     * Constructor.
     */
    public InMemoryCache() {
        this.refreshTokenStore = new InMemoryTokenStore();
    }

    @Override
    public void init(@NonNull Properties properties) {
        logger.info("Starting token store");
        this.refreshTokenStore.init(properties);

        logger.info("Started cache");
    }

    @Override
    public void teardown() {
        logger.info("Stopping token store");
        this.refreshTokenStore.teardown();

        logger.info("Stopped cache");
    }

    @Override
    public RefreshTokenStore getTokenStore() {
        return this.refreshTokenStore;
    }
}
