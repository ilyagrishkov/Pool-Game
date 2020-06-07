package nl.tudelft.cse.sem.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Optional;
import java.util.Properties;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.http.WebService;
import nl.tudelft.cse.sem.server.jwt.JWTManager;
import nl.tudelft.cse.sem.server.storage.cache.Cache;
import nl.tudelft.cse.sem.server.storage.database.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Builder
@Data
public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    @NonNull
    private final Properties properties;

    @NonNull
    private final JWTManager jwtManager;

    @NonNull
    private final Database database;

    @NonNull
    private final Cache cache;

    @NonNull
    private final Javalin javalin;

    @NonNull
    private final WebService webService;

    /**
     * Starts the pool server.
     */
    public void start() {
        logger.info("Starting server");

        logger.info("Starting JWT manager");
        this.jwtManager.init();

        logger.info("Starting database");
        this.database.init(this.properties);

        logger.info("Starting cache");
        this.cache.init(this.properties);

        logger.info("Starting web server");
        this.webService.initialize(this.javalin, this);
        this.webService.start();
    }

    /**
     * Attempts to gracefully stop the server.
     */
    public void stop() {
        logger.info("Stopping server");

        logger.info("Stopping webs server");
        this.webService.stop();

        logger.info("Stopping JWT manager");
        this.jwtManager.teardown();

        logger.info("Stopping database");
        this.database.teardown();

        logger.info("Stopping cache");
        this.cache.teardown();

        logger.info("Stopped server");
    }

    /**
     * Retrieves the IP from the given {@link Context} and prints a warning if nothing
     * could be found.
     *
     * @param context Context to retrieve IP from
     * @return A string representing the IP
     */
    public String retrieveIP(Context context) {
        Optional<String> ipFromHeader = Optional.ofNullable(context.header("X-Forwarded-For"));

        if (ipFromHeader.isEmpty()) {
            logger.warn("Could not determine IP from 'X-Forwarded-For' header. "
                + "Are you running the server without a load balancer / reverse proxy?");
        }

        return ipFromHeader.orElse(context.ip());
    }
}
