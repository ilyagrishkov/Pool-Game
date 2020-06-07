package nl.tudelft.cse.sem.server.http.endpoint;

import io.javalin.http.Context;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles simple ping requests.
 */
@Service(path = Endpoints.PING)
public class PingEndpoint implements Endpoint {
    private static final Logger logger = LogManager.getLogger(PingEndpoint.class);

    @Override
    public void handle(@NonNull Server server, @NonNull Context context) {
        logger.info("Ping from " + server.retrieveIP(context) + "!");

        // Set OK status
        context.status(HttpStatus.OK_200);

        // Set response
        context.json(GenericResponse.builder().success(true).errorMessage("pong!").build());
    }
}
