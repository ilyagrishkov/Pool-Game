package nl.tudelft.cse.sem.server.http.endpoint;

import io.javalin.http.Context;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;

/**
 * Interface representing an endpoint the client can make requests to.
 */
public interface Endpoint {
    /**
     * Handle the request to the endpoint.
     *
     * @param server A {@link Server} instance
     * @param context The {@link Context} for the request
     */
    void handle(@NonNull Server server, @NonNull Context context);
}
