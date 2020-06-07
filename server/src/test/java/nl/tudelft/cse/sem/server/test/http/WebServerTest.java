package nl.tudelft.cse.sem.server.test.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.http.WebServer;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reflections.Reflections;

public class WebServerTest {
    private transient Javalin javalin;
    private transient JavalinConfig javalinConfig;
    private transient Server server;
    private transient WebServer webServer;

    /**
     * Sets up the dependencies and server.
     */
    @BeforeEach
    public void setUp() {
        this.javalin = Mockito.mock(Javalin.class);
        this.javalinConfig = Mockito.mock(JavalinConfig.class);
        this.javalin.config = this.javalinConfig;
        this.server = Mockito.mock(Server.class);

        this.webServer = new WebServer();

        when(this.javalin.exception(any(), any())).thenReturn(this.javalin);
        when(this.javalin.error(anyInt(), any())).thenReturn(this.javalin);

        // Call init function
        this.webServer.initialize(this.javalin, this.server);
    }

    @Test
    public void shouldCallInitMethodsOnInit() {
        // Get amount of endpoints
        int endpoints = (int) new Reflections("nl.tudelft.cse.sem.server.http.endpoint")
            .getTypesAnnotatedWith(Service.class).stream()
            .filter(Endpoint.class::isAssignableFrom).count();

        // Verify dependencies were initialized
        verify(this.javalin, times(endpoints)).addHandler(any(), anyString(), any());
    }

    @Test
    public void shouldCallStartMethodsOnStart() {
        // Call init function
        this.webServer.start();

        // Verify dependencies were initialized
        verify(this.javalin, times(1)).start();
    }

    @Test
    public void shouldCallTeardownMethodsOnStop() {
        // Call stop function
        this.webServer.stop();

        // Verify dependencies were stopped
        verify(this.javalin, times(1)).stop();
    }
}
