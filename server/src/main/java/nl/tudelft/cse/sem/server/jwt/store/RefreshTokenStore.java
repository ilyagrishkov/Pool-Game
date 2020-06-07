package nl.tudelft.cse.sem.server.jwt.store;

import java.util.Properties;
import java.util.UUID;
import lombok.NonNull;

/**
 * Interface for the refresh token store. This should handle adding, verifying and removing
 * refresh tokens used for JWT.
 */
public interface RefreshTokenStore {
    /**
     * Prepares the token store for use. Things like DB connections happen here.
     */
    void init(@NonNull Properties properties);

    /**
     * Gives the token store a chance to gracefully shutdown.
     */
    void teardown();

    /**
     * Returns a boolean indicating the given token matches the one stored in the store
     * for the given uuid.
     *
     * @param token A {@link String} representing the token
     * @param uuid  The user's {@link UUID}
     * @return A boolean which will be true if the token is correct, and false otherwise.
     */
    boolean correctRefreshToken(@NonNull String token, @NonNull UUID uuid);

    /**
     * Stores the given refresh token for the given uuid.
     *
     * @param token A {@link String} representing the refresh token
     * @param uuid  The user's {@link UUID}
     */
    void addRefreshToken(@NonNull String token, @NonNull UUID uuid);

    /**
     * Removed the refresh token matched with the given UUID from the store.
     *
     * @param uuid The user's {@link UUID}
     */
    void removeRefreshToken(@NonNull UUID uuid);
}
