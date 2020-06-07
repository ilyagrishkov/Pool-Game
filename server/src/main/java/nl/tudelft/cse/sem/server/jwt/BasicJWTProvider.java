package nl.tudelft.cse.sem.server.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import lombok.Builder;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple implementation for the signing and verifying of tokens.
 */
@Builder
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public class BasicJWTProvider implements JWTProvider {
    private static final Logger logger = LogManager.getLogger(JWTProvider.class);

    @NonNull
    private final String issuer;

    @NonNull
    private final Algorithm algorithm;

    @NonNull
    private final JWTVerifier verifier;

    @NonNull
    private final Properties properties;

    @Override
    public Optional<String> signToken(@NonNull JWTCreator.Builder tokenBuilder) {
        try {
            // Create and sign token
            String token = tokenBuilder
                .withIssuer(this.issuer)
                .withExpiresAt(this.getDateInFuture(Integer.parseInt(
                    this.properties.getOrDefault("JWT_VALID_MIN", "15").toString())))
                .sign(this.algorithm);

            return Optional.of(token);
        } catch (JWTCreationException exception) {
            logger.error("Could not create/sign JWT token: " + exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<DecodedJWT> decodeToken(@NonNull String token) {
        try {
            return Optional.of(this.verifier.verify(token));
        } catch (JWTVerificationException exception) {
            logger.debug("Token '" + token + "' was invalid");

            return Optional.empty();
        }
    }

    /**
     * Returns a new {@link Date} which is the given amount of minutes in the future.
     *
     * @param minutes Minutes to skip
     * @return A new {@link Date} instance
     */
    private Date getDateInFuture(int minutes) {
        Calendar currentDate = Calendar.getInstance();

        return new Date(currentDate.getTimeInMillis() + minutes * 60 * 1000);
    }
}
