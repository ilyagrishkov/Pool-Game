package nl.tudelft.cse.sem.server.jwt.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple {@link RefreshTokenStore} implementation, which stores the tokens in memory.
 * Warning: This simple implementation does not care about expiration, so the refresh tokens
 * are valid for as long as the server is running (or when they get deleted manually)
 */
public class InMemoryTokenStore implements RefreshTokenStore {
    private static final Logger logger = LogManager.getLogger(InMemoryTokenStore.class);
    private transient Map<UUID, String> tokens;

    @Override
    public void init(@NonNull Properties properties) {
        logger.debug("Creating map to store tokens");
        this.tokens = new HashMap<>();

        logger.info("Started token store");
    }

    @Override
    public void teardown() {
        logger.info("Stopped token store");
    }

    @Override
    public boolean correctRefreshToken(@NonNull String token, @NonNull UUID uuid) {
        logger.debug("Checking refresh token for uuid " + uuid);
        return this.tokens.containsKey(uuid) && this.tokens.get(uuid).equals(token);
    }

    @Override
    public void addRefreshToken(@NonNull String token, @NonNull UUID uuid) {
        logger.debug("Adding refresh token for uuid " + uuid);
        this.tokens.put(uuid, token);
    }

    @Override
    public void removeRefreshToken(@NonNull UUID uuid) {
        logger.debug("Removing refresh token for uuid " + uuid);
        this.tokens.remove(uuid);
    }
}
