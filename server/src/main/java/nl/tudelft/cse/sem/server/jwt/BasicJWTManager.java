package nl.tudelft.cse.sem.server.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.jwt.store.RefreshTokenStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A basic class which implements all minimum functions required to get JWT working.
 */
@Builder
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public class BasicJWTManager implements JWTManager {
    private static final Logger logger = LogManager.getLogger(BasicJWTManager.class);
    private final transient SecureRandom secureRandom = new SecureRandom();

    @NonNull
    private final JWTProvider jwtProvider;

    @NonNull
    private final RefreshTokenStore tokenStore;

    @NonNull
    private final UserStore userStore;

    @NonNull
    private final Properties properties;

    @Override
    public void init() {
        logger.info("Starting token store");
        this.tokenStore.init(this.properties);

        logger.info("Started JWT manager");
    }

    @Override
    public void teardown() {
        logger.info("Stopping token store");
        this.tokenStore.teardown();

        logger.info("Stopped JWT manager");
    }

    @Override
    public Optional<String> generateToken(@NonNull User user) {
        logger.debug("Generating a token for user " + user);
        // Create an empty jwt builder
        JWTCreator.Builder builder = JWT.create();

        // Add the claims
        user.addToToken(builder);

        // Sign it and return
        return this.jwtProvider.signToken(builder);
    }

    @Override
    public String generateRefreshToken(@NonNull User user) {
        logger.debug("Generating refresh token for " + user);
        // Create a random secure string for use as a refresh token
        byte[] randomBytes = new byte[32];
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        secureRandom.nextBytes(randomBytes);
        String token = encoder.encodeToString(randomBytes);

        // Store and return
        this.tokenStore.addRefreshToken(token, user.getUuid());
        return token;
    }

    @Override
    public Optional<String> refreshToken(@NonNull String refreshToken, @NonNull UUID uuid) {
        logger.debug("Refreshing token for user with uuid '" + uuid + "'");

        // If refresh token is valid, return a new token
        if (this.tokenStore.correctRefreshToken(refreshToken, uuid)) {
            Optional<User> user = this.userStore.loadUserFromID(uuid);

            // Check if the user has been deleted
            if (user.isEmpty()) {
                logger.debug("User has been deleted, not generating a new token");
                return Optional.empty();
            }

            logger.debug("Generating new token based on refresh token");
            return this.generateToken(user.get());
        }

        logger.debug("Could not refresh token");
        // Else just return and empty optional signaling failure
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserFromContext(@NonNull Context context) {
        // First get authorization header
        Optional<String> header = Optional.ofNullable(context.header("Authorization"));

        logger.debug("Retrieving user from context: " + header);

        // Then return the get the token and build the user
        return header
            .flatMap(value -> {
                String[] valueSplit = value.split("\\s");

                // Check if not the bearer token
                if (valueSplit.length != 2 || !valueSplit[0].equals("Bearer")) {
                    logger.debug("Received an unknown authorization header: " + header.get());
                    return Optional.empty();
                }

                // Get token and verify
                Optional<DecodedJWT> decodedToken = this.jwtProvider.decodeToken(valueSplit[1]);

                // If token is empty return empty optional to indicate failure
                if (decodedToken.isEmpty()) {
                    logger.debug("Could not decode token");
                    return Optional.empty();
                }

                // Build user, log a message and return
                User user = User.buildFromToken(decodedToken.get());

                // Make sure the user actually exists
                if (!this.userStore.userExists(user.getUuid())) {
                    return Optional.empty();
                }

                logger.debug("Successfully reconstructed user '" + user + "'");
                return Optional.of(user);
            });
    }

    @Override
    public void invalidateRefreshToken(@NonNull User user) {
        logger.debug("Invalidating refresh token for user " + user);
        this.tokenStore.removeRefreshToken(user.getUuid());
    }
}
