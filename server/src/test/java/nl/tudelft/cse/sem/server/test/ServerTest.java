package nl.tudelft.cse.sem.server.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.http.WebService;
import nl.tudelft.cse.sem.server.jwt.JWTManager;
import nl.tudelft.cse.sem.server.storage.cache.Cache;
import nl.tudelft.cse.sem.server.storage.database.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServerTest {
    private transient Properties properties;
    private transient JWTManager manager;
    private transient Database database;
    private transient Cache cache;
    private transient Javalin javalin;
    private transient JavalinConfig javalinConfig;
    private transient Server server;
    private transient WebService webService;

    /**
     * Sets up the dependencies and server.
     */
    @BeforeEach
    public void setUp() {
        this.properties = new Properties();
        this.manager = mock(JWTManager.class);
        this.database = mock(Database.class);
        this.cache = mock(Cache.class);
        this.javalin = mock(Javalin.class);
        this.javalinConfig = mock(JavalinConfig.class);
        this.javalin.config = this.javalinConfig;
        this.webService = Mockito.mock(WebService.class);

        this.server = Server.builder()
            .properties(this.properties)
            .jwtManager(this.manager)
            .database(this.database)
            .cache(this.cache)
            .javalin(this.javalin)
            .webService(this.webService)
            .build();
    }

    @Test
    public void shouldCallInitMethodsOnStart() {
        // Setup javalin
        when(this.javalin.exception(any(), any())).thenReturn(this.javalin);
        when(this.javalin.error(anyInt(), any())).thenReturn(this.javalin);

        // Call start function
        this.server.start();

        // Verify dependencies were initialized
        verify(this.manager, times(1)).init();
        verify(this.database, times(1)).init(any());
        verify(this.cache, times(1)).init(any());
        verify(this.webService, times(1)).start();
    }

    @Test
    public void shouldCallTeardownMethodsOnStop() {
        // Call stop function
        this.server.stop();

        // Verify dependencies were stopped
        verify(this.manager, times(1)).teardown();
        verify(this.database, times(1)).teardown();
        verify(this.cache, times(1)).teardown();
        verify(this.webService, times(1)).stop();
    }

    @SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about IP
    @Test
    public void shouldRetrieveIPFromHeader() {
        String randomIP = "1.1.1.1";

        // Create dependencies
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        Context context = ContextUtil.init(req, res);

        // Setup dependencies
        when(req.getHeader("X-Forwarded-For")).thenReturn(randomIP);
        when(req.getRemoteAddr()).thenReturn("null");

        // Make sure the IP is retrieved from the header
        assertEquals(randomIP, this.server.retrieveIP(context));
    }

    @SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about IP
    @Test
    public void shouldRetrieveIPFromContext() {
        String randomIP = "1.1.1.1";

        // Create dependencies
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        Context context = ContextUtil.init(req, res);

        // Setup dependencies
        when(req.getRemoteAddr()).thenReturn(randomIP);

        // Make sure the IP is retrieved from the header
        assertEquals(randomIP, this.server.retrieveIP(context));
    }
}
