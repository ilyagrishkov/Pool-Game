package nl.tudelft.cse.sem.server.test.http.endpoint;

import nl.tudelft.cse.sem.server.http.endpoint.PingEndpoint;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PingEndpointTest extends EndpointTest {
    /**
     * Setup dependencies.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.endpoint = new PingEndpoint();
    }

    @Test
    public void shouldReturnPongOnPing() {
        this.endpoint.handle(this.server, this.context);

        verifyStatusCode(HttpStatus.OK_200);
        verifyBody(GenericResponse.builder().success(true).errorMessage("pong!").build());
    }
}
