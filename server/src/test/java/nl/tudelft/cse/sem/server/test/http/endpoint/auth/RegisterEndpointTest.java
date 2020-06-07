package nl.tudelft.cse.sem.server.test.http.endpoint.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ConflictResponse;
import java.io.IOException;
import java.util.Optional;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.http.endpoint.auth.RegisterEndpoint;
import nl.tudelft.cse.sem.server.test.http.endpoint.EndpointTest;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import nl.tudelft.cse.sem.shared.entity.auth.Registration;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterEndpointTest extends EndpointTest {
    private transient UserStore userStore;
    private transient String username;
    private transient String password;

    /**
     * Setup dependencies.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.username = "test";
        this.password = "test_password";
        this.userStore = mock(UserStore.class);
        this.endpoint = new RegisterEndpoint();

        when(this.database.getUserStore()).thenReturn(this.userStore);
    }

    @Test
    public void shouldReturnTokenWhenCorrectRegistration() throws IOException {
        setRequestBody(Registration.builder()
            .username(this.username).password(this.password).displayName(this.username).build());
        when(this.userStore.addUser(any(), eq(this.password), eq(null)))
            .thenReturn(Optional.of(this.user));

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        verifyBody(GenericResponse.builder().success(true).build());
    }

    @Test
    public void shouldThrowConflictWhenKnownUser() throws IOException {
        setRequestBody(Registration.builder()
            .username(this.username).password(this.password).displayName(this.username).build());
        when(this.userStore.addUser(any(), eq(this.password), eq(null)))
            .thenReturn(Optional.empty());

        assertThrows(ConflictResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNothing() throws IOException {
        Registration registration = new Registration();

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));

        setRequestBody(registration);
        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNoUsername() throws IOException {
        Registration registration = new Registration();

        registration.setDisplayName(this.username);
        registration.setPassword(this.password);
        setRequestBody(registration);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNoPassword() throws IOException {
        Registration registration = new Registration();

        registration.setDisplayName(this.username);
        registration.setUsername(this.username);
        setRequestBody(registration);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNoDisplayName() throws IOException {
        Registration registration = new Registration();

        registration.setPassword(this.password);
        registration.setUsername(this.username);
        setRequestBody(registration);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenEmpty() throws IOException {
        Registration registration = new Registration();

        registration.setUsername("");
        registration.setDisplayName(this.username);
        registration.setPassword(this.password);
        setRequestBody(registration);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }
}
