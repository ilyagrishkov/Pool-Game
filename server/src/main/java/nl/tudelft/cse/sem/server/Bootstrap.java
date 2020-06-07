package nl.tudelft.cse.sem.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.Javalin;
import java.util.Properties;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.http.WebServer;
import nl.tudelft.cse.sem.server.jwt.BasicJWTManager;
import nl.tudelft.cse.sem.server.jwt.BasicJWTProvider;
import nl.tudelft.cse.sem.server.jwt.JWTManager;
import nl.tudelft.cse.sem.server.jwt.JWTProvider;
import nl.tudelft.cse.sem.server.storage.cache.Cache;
import nl.tudelft.cse.sem.server.storage.cache.CacheType;
import nl.tudelft.cse.sem.server.storage.database.Database;
import nl.tudelft.cse.sem.server.storage.database.DatabaseType;
import nl.tudelft.cse.sem.server.storage.database.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.hibernate.cfg.Environment;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    /**
     * Main entry point of the server.
     *
     * @param args Program arguments
     */
    // PMD can't handle 'new' java 8 things
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public static void main(String... args) {
        logger.info("Bootstrapping server");

        // Create properties and find settings
        Properties properties = getDefaultDatabaseProperties();

        // Add variables
        getEnvironmentVariable(properties, "JWT_SECRET");
        getEnvironmentVariable(properties, "DATABASE");
        getEnvironmentVariable(properties, "CACHE");
        getEnvironmentVariable(properties, HibernateUtil.MYSQL_USER);
        getEnvironmentVariable(properties, HibernateUtil.MYSQL_PASS);
        getEnvironmentVariable(properties, HibernateUtil.MYSQL_URL);
        getEnvironmentVariable(properties, HibernateUtil.MYSQL_DATABASE);

        // Transfer 'easy to read' variables to real variables
        properties.setProperty(Environment.USER, properties.getProperty(HibernateUtil.MYSQL_USER));
        properties.setProperty(Environment.PASS, properties.getProperty(HibernateUtil.MYSQL_PASS));
        properties.setProperty(Environment.URL, "jdbc:mysql://"
            + properties.getProperty(HibernateUtil.MYSQL_URL) + "/"
            + properties.getProperty(HibernateUtil.MYSQL_DATABASE) + "?"
            + "autoReconnect=true&createDatabaseIfNotExist=true");

        // Create database and cache
        Database database = DatabaseType.valueOf(properties.getProperty("DATABASE")).create();
        Cache cache = CacheType.valueOf(properties.getProperty("CACHE")).create();

        // Create JWT provider and manager
        JWTProvider jwtProvider = buildDefaultProvider(properties);
        JWTManager jwtManager = BasicJWTManager.builder()
            .jwtProvider(jwtProvider)
            .tokenStore(cache.getTokenStore())
            .userStore(database.getUserStore())
            .properties(properties)
            .build();

        // Configure javalin
        Javalin javalin = Javalin.create(config -> {
            // Tweak jetty config so it works in a docker container
            config.server(() -> {
                    org.eclipse.jetty.server.Server server =
                        new org.eclipse.jetty.server.Server();
                    HttpConfiguration httpConfiguration = new HttpConfiguration();
                    ServerConnector http = new ServerConnector(server,
                        new HttpConnectionFactory(httpConfiguration));

                    // Listen on everything
                    http.setHost("0.0.0.0");
                    http.setPort(8080);

                    server.setConnectors(new Connector[] {http});
                    return server;
                }
            );
        });

        // Create server instance
        Server server = Server.builder()
            .properties(properties)
            .jwtManager(jwtManager)
            .database(database)
            .cache(cache)
            .javalin(javalin)
            .webService(new WebServer())
            .build();

        // Start the server
        server.start();

        // Create shutdown hook to intercept SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    /**
     * Method which attempts to find the given environment variable and puts it into the given
     * properties.
     *
     * @param properties Properties to add to
     * @param env        Environment variable to look for
     */
    // Ignoring PMD warning for System.exit(),
    // because the bootstrapper should be able to stop starting
    @SuppressWarnings("PMD.DoNotCallSystemExit")
    private static void getEnvironmentVariable(@NonNull Properties properties,
                                               @NonNull String env) {
        // Get property
        String result = System.getenv(env);

        // Check if the default variable was not changed/present
        if (result == null || result.equalsIgnoreCase("null")) {
            logger.fatal("Could not find " + env + "!");
            System.exit(1);
        }

        // Set it and print result (provided it is not a secret)
        properties.setProperty(env, result);
        logger.info("Found setting for " + env + ": "
            + (!env.contains("SECRET") && !env.contains("PASS") && !env.contains("pass")
            ? result : "****"));
    }

    /**
     * Builds the default {@link JWTProvider} for use by the server.
     *
     * @param properties Properties to retreive settings from
     * @return A configured {@link JWTProvider}
     */
    private static JWTProvider buildDefaultProvider(@NonNull Properties properties) {
        final int bestSecretSize = 64;

        // Print a quick warning in case the secret is lower than 512 bits (64 chars)
        if (properties.getProperty("JWT_SECRET").length() < bestSecretSize) {
            logger.warn("You are using a weak JWT secret, "
                + "consider using a random string of 64 chars!");
        }

        // Create issuer and algorithm vars
        final String issuer = "tudelft-pool";
        final Algorithm algorithm = Algorithm.HMAC256(properties.getProperty("JWT_SECRET"));

        // Build the actual verifier
        final JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build();

        // Create and return the JWT provider
        return BasicJWTProvider.builder()
            .issuer(issuer)
            .algorithm(algorithm)
            .verifier(verifier)
            .properties(properties)
            .build();
    }

    /**
     * Returns default MySQL properties.
     *
     * @return {@link Properties} containing the default settings.
     */
    public static Properties getDefaultDatabaseProperties() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        properties.put(Environment.URL,
            "jdbc:mysql://projects-db.ewi.tudelft.nl/projects_SEM-pool86?"
                + "useSSL=true&autoReconnect=true&createDatabaseIfNotExist=true");
        properties.put(Environment.USER, "pu_SEM-pool86");
        properties.put(Environment.PASS, "NULL");

        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        return properties;
    }
}
