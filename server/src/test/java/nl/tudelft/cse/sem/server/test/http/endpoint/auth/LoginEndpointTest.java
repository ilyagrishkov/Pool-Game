package nl.tudelft.cse.sem.server.test.http.endpoint.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import java.io.IOException;
import java.util.Optional;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.http.endpoint.auth.LoginEndpoint;
import nl.tudelft.cse.sem.server.test.http.endpoint.EndpointTest;
import nl.tudelft.cse.sem.shared.entity.auth.Credentials;
import nl.tudelft.cse.sem.shared.entity.auth.TokenResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginEndpointTest extends EndpointTest {
    private transient UserStore userStore;
    private transient String username;
    private transient String password;
    private transient String token;
    private transient String refreshToken;

    /**
     * Setup dependencies.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.username = "test";
        this.password = "test_password";
        this.token = "token";
        this.refreshToken = "refresh_token";
        this.userStore = mock(UserStore.class);
        this.endpoint = new LoginEndpoint();

        when(this.database.getUserStore()).thenReturn(this.userStore);
    }

    @Test
    public void shouldReturnTokenWhenCorrectPassword() throws IOException {
        setRequestBody(
            Credentials.builder().username(this.username).password(this.password).build());
        when(this.userStore.loadUserFromName(this.username)).thenReturn(Optional.of(this.user));
        when(this.userStore.verifyPassword(this.user, this.password)).thenReturn(true);
        when(this.userStore.verifyMultiFactorAuthenticationToken(eq(this.user), any()))
            .thenReturn(true);
        when(this.jwtManager.generateToken(this.user)).thenReturn(Optional.of(this.token));
        when(this.jwtManager.generateRefreshToken(this.user)).thenReturn(this.refreshToken);

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        verifyBody(TokenResponse.builder()
            .refreshToken(this.refreshToken).token(token).success(true).build());
    }

    @Test
    public void shouldThrowUnauthorizedWhenUnknownUser() throws IOException {
        setRequestBody(
            Credentials.builder().username(this.password).password(this.password).build());
        when(this.userStore.loadUserFromName(this.password)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowUnauthorizedWhenBadPassword() throws IOException {
        setRequestBody(
            Credentials.builder().username(this.username).password(this.password).build());
        when(this.userStore.loadUserFromName(this.username)).thenReturn(Optional.of(this.user));
        when(this.userStore.verifyPassword(this.user, this.password)).thenReturn(false);

        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowUnauthorizedWhenNoTokenGenerated() throws IOException {
        setRequestBody(
            Credentials.builder().username(this.username).password(this.password).build());
        when(this.userStore.loadUserFromName(this.username)).thenReturn(Optional.of(this.user));
        when(this.userStore.verifyMultiFactorAuthenticationToken(eq(this.user), any()))
            .thenReturn(true);
        when(this.userStore.verifyPassword(this.user, this.password)).thenReturn(true);
        when(this.jwtManager.generateToken(this.user)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowUnauthorizedWhenIncorrectMultiFactor() throws IOException {
        setRequestBody(
            Credentials.builder().username(this.username).password(this.password).build());
        when(this.userStore.loadUserFromName(this.username)).thenReturn(Optional.of(this.user));
        when(this.userStore.verifyMultiFactorAuthenticationToken(eq(this.user), any()))
            .thenReturn(false);
        when(this.userStore.verifyPassword(this.user, this.password)).thenReturn(true);

        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNothing() throws IOException {
        Credentials credentials = new Credentials();

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));

        setRequestBody(credentials);
        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNoUsername() throws IOException {
        Credentials credentials = new Credentials();

        credentials.setPassword(this.password);
        setRequestBody(credentials);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenNoPassword() throws IOException {
        Credentials credentials = new Credentials();

        credentials.setUsername(this.username);
        setRequestBody(credentials);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenEmpty() throws IOException {
        Credentials credentials = new Credentials();

        credentials.setUsername("");
        credentials.setPassword(this.password);
        setRequestBody(credentials);

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }
}
