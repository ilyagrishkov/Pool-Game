package nl.tudelft.cse.sem.server.http;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;
import lombok.Generated;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.reflections.Reflections;

/**
 * Implementation for {@link WebService}, which actually handles the requests.
 */
public class WebServer implements WebService {
    private static final Logger logger = LogManager.getLogger(WebServer.class);

    private transient Javalin javalin;
    private transient Server server;

    @Override
    // PMD can't handle 'new' java 8 things
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void initialize(Javalin javalin, Server server) {
        this.javalin = javalin;
        this.server = server;

        logger.info("Adding handlers");
        this.javalin.config.requestLogger(((ctx, ms) -> {
            // Setting logger
            logger.info("Request [" + ms + " ms] -> " + ctx.method() + " " + ctx.path()
                + " (" + server.retrieveIP(ctx) + ")");
        }));

        this.addErrorHandler(this.javalin);

        logger.info("Adding endpoints");
        this.loadEndpoints("nl.tudelft.cse.sem.server.http.endpoint");
    }

    @Override
    public void start() {
        logger.info("Starting javalin");
        this.javalin.start();
    }

    @Override
    public void stop() {
        logger.info("Stopping web server");
        logger.info("Stopping javalin");
        this.javalin.stop();
    }

    /**
     * Adds all needed error handlers to the given {@link Javalin} instance.
     *
     * @param javalin Instance to add handlers to
     */
    @Generated // This can be ignored, since these are all anon. handlers
    // PMD can't handle 'new' java 8 things
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void addErrorHandler(Javalin javalin) {
        javalin
            .exception(NotFoundResponse.class, ((exception, ctx) -> {
                ctx.status(HttpStatus.NOT_FOUND_404);
            }))
            .exception(BadRequestResponse.class, (exception, ctx) -> {
                logger.info("Received a bad request from " + server.retrieveIP(ctx)
                    + ": " + ctx.body());
                ctx.status(HttpStatus.BAD_REQUEST_400);
            })
            .exception(InternalServerErrorResponse.class, (exception, ctx) -> {
                logger.error("Request from " + server.retrieveIP(ctx)
                    + " resulted in an exception: " + ctx.body());
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            })
            .exception(UnauthorizedResponse.class, (exception, ctx) -> {
                ctx.status(HttpStatus.UNAUTHORIZED_401);
                ctx.json(GenericResponse.builder()
                    .success(false)
                    .errorMessage(Optional.ofNullable(exception.getMessage())
                        .orElse("Unauthorized"))
                    .build());
            })
            .exception(ConflictResponse.class, (exception, ctx) ->
                ctx.status(HttpStatus.CONFLICT_409))
            .error(HttpStatus.BAD_REQUEST_400, ctx ->
                ctx.json(GenericResponse.builder()
                    .success(false)
                    .errorMessage("Bad request")
                    .build()))
            .error(HttpStatus.NOT_FOUND_404, ctx ->
                ctx.json(GenericResponse.builder()
                    .success(false)
                    .errorMessage("Not found")
                    .build()))
            .error(HttpStatus.INTERNAL_SERVER_ERROR_500, ctx ->
                ctx.json(GenericResponse.builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .build()))
            .error(HttpStatus.CONFLICT_409, ctx ->
                ctx.json(GenericResponse.builder()
                    .success(false)
                    .errorMessage("Conflict")
                    .build()));
    }

    /**
     * Loads all endpoints in the given path.
     *
     * @param path Path to check
     */
    // PMD can't handle 'new' java 8 things
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void loadEndpoints(String path) {
        Reflections reflections = new Reflections(path);
        Set<Class<?>> endpoints = reflections.getTypesAnnotatedWith(Service.class);

        // Loop over all possible endpoints and check if they actually are endpoints
        for (Class<?> clazz : endpoints) {
            if (Endpoint.class.isAssignableFrom(clazz)) {
                try {
                    logger.debug("Found endpoint: " + clazz.getSimpleName());

                    // Get service information and attempt to add to the endpoint list
                    Endpoint endpoint = (Endpoint) clazz.getDeclaredConstructor().newInstance();
                    Service endpointInfo = clazz.getAnnotation(Service.class);

                    this.javalin.addHandler(endpointInfo.method(),
                        endpointInfo.path(),
                        ctx -> endpoint.handle(server, ctx));
                    logger.debug("Loaded endpoint: " + clazz.getSimpleName()
                        + " (" + endpointInfo.method() + " " + endpointInfo.path() + ")");
                } catch (InstantiationException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | IllegalAccessException e) {
                    logger.error("Failed to initialize endpoint '"
                        + clazz.getSimpleName() + "': " + e.getMessage());
                }
            }
        }
    }
}
