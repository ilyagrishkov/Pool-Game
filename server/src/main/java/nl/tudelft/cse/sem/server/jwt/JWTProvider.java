package nl.tudelft.cse.sem.server.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;
import lombok.NonNull;

/**
 * Simple interface which has two functions: sign and verify tokens.
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public interface JWTProvider {
    /**
     * Method which takes a {@link JWTCreator.Builder} and simply signs it.
     *
     * @param tokenBuilder {@link JWTCreator.Builder} which contains the claims that
     *                     should be signed
     * @return An {@link Optional}, which contains the token. If not, the claim could
     *     not be signed
     */
    Optional<String> signToken(@NonNull JWTCreator.Builder tokenBuilder);

    /**
     * Method which takes a token, and verifies it.
     *
     * @param token {@link String} representing the token
     * @return An {@link Optional} containing the decoded token.
     *     If not, the token could not be verified
     */
    Optional<DecodedJWT> decodeToken(@NonNull String token);
}
