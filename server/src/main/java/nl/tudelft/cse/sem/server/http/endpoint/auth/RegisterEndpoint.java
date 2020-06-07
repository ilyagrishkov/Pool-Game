package nl.tudelft.cse.sem.server.http.endpoint.auth;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import nl.tudelft.cse.sem.shared.entity.auth.Registration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles register requests.
 */
@Service(method = HandlerType.POST, path = Endpoints.REGISTER)
public class RegisterEndpoint implements Endpoint {
    private static final Logger logger = LogManager.getLogger(RegisterEndpoint.class);

    @Override
    public void handle(@NonNull Server server, @NonNull Context context) {
        Registration registration = context.bodyAsClass(Registration.class);

        // Return a bad request if the client didn't send everything
        if (registration == null || registration.getDisplayName() == null
            || registration.getPassword() == null || registration.getUsername() == null
            || !registration.correct()) {
            throw new BadRequestResponse();
        }

        // Save user and return conflict response if the user didn't get saved (aka name occupied)
        Optional<User> savedUser = server.getDatabase().getUserStore()
            .addUser(User.builder()
                .loginName(registration.getUsername())
                .displayName(registration.getDisplayName())
                .uuid(UUID.randomUUID()).build(), registration.getPassword(),
                registration.getMultiFactorAuthenticationToken());

        // Return an error if the user could not be saved
        if (savedUser.isEmpty()) {
            logger.info("Could not create a new user '" + registration.getUsername()
                + "': Conflict");
            throw new ConflictResponse();
        }

        // Set OK status and response
        logger.info("Created a new account '" + registration.getUsername() + "' from "
            + server.retrieveIP(context));
        context.status(HttpStatus.OK_200);
        context.json(GenericResponse.builder().success(true).build());
    }
}
