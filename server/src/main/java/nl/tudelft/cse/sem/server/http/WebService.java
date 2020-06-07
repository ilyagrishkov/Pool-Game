package nl.tudelft.cse.sem.server.http;

import io.javalin.Javalin;
import nl.tudelft.cse.sem.server.Server;

/**
 * Interface for a web server. Should handle the requests and pass them to endpoint handlers.
 */
public interface WebService {
    /**
     * Initialize the web server. This will loads endpoints etc.
     *
     * @param javalin The {@link Javalin} instance.
     * @param server The {@link Server} instance.
     */
    void initialize(Javalin javalin, Server server);

    /**
     * Start the web server.
     */
    void start();

    /**
     * Stop the web server.
     */
    void stop();
}
