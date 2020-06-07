package nl.tudelft.cse.sem.server.http.endpoint.auth;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.UnauthorizedResponse;
import java.util.Optional;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.auth.Credentials;
import nl.tudelft.cse.sem.shared.entity.auth.TokenResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles authentication requests.
 */
@Service(method = HandlerType.POST, path = Endpoints.LOGIN)
public class LoginEndpoint implements Endpoint {
    private static final Logger logger = LogManager.getLogger(LoginEndpoint.class);

    @Override
    public void handle(@NonNull Server server, @NonNull Context context) {
        Credentials credentials = context.bodyAsClass(Credentials.class);

        // Return a bad request if the client didn't send everything
        if (credentials == null || credentials.getPassword() == null
            || credentials.getUsername() == null || !credentials.correct()) {
            throw new BadRequestResponse();
        }

        Optional<User> userFromName = server.getDatabase().getUserStore()
            .loadUserFromName(credentials.getUsername());

        // Check the password if the user was found
        if (userFromName.isPresent()) {
            User user = userFromName.get();

            // If user credentials are correct, return tokens
            if (server.getDatabase().getUserStore()
                .verifyPassword(user, credentials.getPassword())) {
                // Quickly make sure the supplied mfa token is correct
                if (!server.getDatabase().getUserStore()
                    .verifyMultiFactorAuthenticationToken(user,
                        credentials.getMultiFactorAuthenticationToken())) {
                    logger.info("Incorrect MFA token for '" + credentials.getUsername()
                        + "' from " + server.retrieveIP(context));
                    throw new UnauthorizedResponse("mfa");
                }

                Optional<String> token = server.getJwtManager().generateToken(user);

                // If token was generated, set status and response
                if (token.isPresent()) {
                    context.status(HttpStatus.OK_200);
                    context.json(TokenResponse.builder()
                        .refreshToken(server.getJwtManager().generateRefreshToken(user))
                        .token(token.get())
                        .success(true).build());

                    logger.info("User '" + user.getLoginName() + "' logged in from "
                        + server.retrieveIP(context));
                    return;
                }

                logger.warn("Could not generate token for user " + user);
            }
        }

        // No user was found or password was incorrect
        logger.info("Failed login attempt for '" + credentials.getUsername() + "' from "
            + server.retrieveIP(context));
        throw new UnauthorizedResponse();
    }
}
