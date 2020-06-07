package nl.tudelft.cse.sem.server.entity;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * An user object used on the server which builds on the shared user object.
 * It adds roles and ways to add claims to tokens and construct itself based on decoded tokens.
 */
@SuperBuilder(toBuilder = true)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends nl.tudelft.cse.sem.shared.entity.User {
    /**
     * Add all claims needed to reconstruct the object to the given {@link JWTCreator.Builder}.
     *
     * @param builder {@link JWTCreator.Builder} to add the claims to
     */
    public void addToToken(@NonNull JWTCreator.Builder builder) {
        builder
            .withClaim("login_name", super.getLoginName())
            .withClaim("display_name", super.getDisplayName())
            .withClaim("uuid", super.getUuid().toString());
    }

    /**
     * Builds a user object from a {@link DecodedJWT} token.
     *
     * @param token {@link DecodedJWT} token to use for construction of the user object
     * @return A {@link User} based on the given {@link DecodedJWT}
     */
    public static User buildFromToken(@NonNull DecodedJWT token) {
        /* Exaggerated syntax to keep this part clear (especially the optional stuff).
         *
         * Small explanation as to why some claims are optional and some are not:
         * Both login_name and display_name do not really matter, since unauthenticated users
         * should indeed have no login- and display-name.
         *
         * UUID also can be null in theory, however passing null into fromString() is a really bad
         * idea. Same goes for the role: it can be null, but it should fall back to a value which
         * is valid and does not crash the server.
         */
        return User.builder()
            .loginName(
                token.getClaim("login_name").asString()
            )
            .displayName(
                token.getClaim("display_name").asString()
            )
            .uuid(
                UUID.fromString(
                    Optional.ofNullable(
                        token.getClaim("uuid").asString()
                    ).orElse(
                        UUID.randomUUID().toString()
                    )
                )
            )
            .build();
    }
}
