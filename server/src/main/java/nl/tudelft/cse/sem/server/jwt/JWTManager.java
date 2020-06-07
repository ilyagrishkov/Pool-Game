package nl.tudelft.cse.sem.server.jwt;

import io.javalin.http.Context;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;

/**
 * Interface which contains the basic functions required to use JWT.
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public interface JWTManager {
    /**
     * Prepares the manager for use.
     */
    void init();

    /**
     * Gives the manager a chance to gracefully shutdown.
     */
    void teardown();

    /**
     * Attempts to create and sign a new token for the given user.
     *
     * @param user The {@link User} who needs a token
     * @return An {@link Optional} which contains the new token.
     *     If not, it could not be generated.
     */
    Optional<String> generateToken(@NonNull User user);

    /**
     * Generates and stores a refresh token for the given user.
     *
     * @param user A {@link User} to generate a token for
     * @return A {@link String} representing the refresh token.
     */
    String generateRefreshToken(@NonNull User user);

    /**
     * Attempts to create and sign a new token based on the given refresh token and uuid.
     *
     * @param refreshToken A {@link String} representing the refresh token
     * @param uuid         The {@link UUID} of the user
     * @return An {@link Optional} which contains the new token.
     *     If not, either the refresh token or the uuid was invalid.
     */
    Optional<String> refreshToken(@NonNull String refreshToken, @NonNull UUID uuid);

    /**
     * Attempts to retrieve the user from the given request context.
     *
     * @param context The request {@link Context}
     * @return An {@link Optional} which contains the user.
     *     If not, the user could not be retrieved from the request context.
     *     If that's the case the user is most likely not logged in or providing a fake token.
     */
    Optional<User> getUserFromContext(@NonNull Context context);

    /**
     * Invalidates the refresh token, effectively logging out the user.
     *
     * @param user The {@link User} to log out
     */
    void invalidateRefreshToken(@NonNull User user);
}
