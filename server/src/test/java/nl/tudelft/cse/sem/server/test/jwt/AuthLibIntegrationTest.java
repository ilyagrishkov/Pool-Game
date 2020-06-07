package nl.tudelft.cse.sem.server.test.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.entity.store.impl.InMemoryUserStore;
import nl.tudelft.cse.sem.server.jwt.BasicJWTManager;
import nl.tudelft.cse.sem.server.jwt.BasicJWTProvider;
import nl.tudelft.cse.sem.server.jwt.JWTManager;
import nl.tudelft.cse.sem.server.jwt.JWTProvider;
import nl.tudelft.cse.sem.server.jwt.store.InMemoryTokenStore;
import nl.tudelft.cse.sem.server.jwt.store.RefreshTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AuthLibIntegrationTest {
    private static final String JWT_SECRET = "SECRET";
    private static final String ISSUER = "pool-game-issuer";

    private transient JWTManager manager;
    private transient UserStore userStore;
    private transient User user;
    private transient Properties properties;
    private transient String password;

    /**
     * Sets up dependencies and manager.
     */
    @BeforeEach
    public void setUp() {
        this.password = "password";
        this.properties = new Properties();
        this.userStore = new InMemoryUserStore();
        RefreshTokenStore tokenStore = new InMemoryTokenStore();
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        JWTProvider provider = BasicJWTProvider.builder()
            .algorithm(algorithm)
            .issuer(ISSUER)
            .verifier(verifier)
            .properties(properties)
            .build();

        this.manager = BasicJWTManager.builder()
            .jwtProvider(provider)
            .properties(properties)
            .tokenStore(tokenStore)
            .userStore(this.userStore)
            .build();

        this.manager.init();
        userStore.init(properties);

        this.user = User.builder()
            .loginName("user1")
            .displayName("user1_display")
            .uuid(UUID.randomUUID())
            .build();
    }

    /**
     * Creates a context with the given token.
     *
     * @param token Token to use
     * @return A new {@link Context}.
     */
    private Context createContextWithToken(String token) {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        // Add header
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Make and return context
        return ContextUtil.init(request, response);
    }

    @Test
    public void generateAndCheckToken() {
        // Add user and make sure it was added
        assertTrue(this.userStore.addUser(this.user, this.password, null).isPresent());

        // Generate token
        Optional<String> token = this.manager.generateToken(this.user);

        // Make sure the token was generated
        assertTrue(token.isPresent());

        // Create context for request
        Context context = this.createContextWithToken(token.get());

        // Get user from 'request'
        Optional<User> userFromRequest = this.manager.getUserFromContext(context);

        // Make sure we got the correct user
        assertTrue(userFromRequest.isPresent());
        assertEquals(this.user.getUuid(), userFromRequest.get().getUuid());
    }

    @Test
    public void generateAndRefreshToken() {
        // Add user and make sure it was added
        assertTrue(this.userStore.addUser(this.user, this.password, null).isPresent());

        // Set expiration time to -1
        this.properties.setProperty("JWT_VALID_MIN", "-1");

        // Generate token
        Optional<String> token = this.manager.generateToken(this.user);

        // Make sure the token was generated
        assertTrue(token.isPresent());

        // Create context for request
        Context context = this.createContextWithToken(token.get());

        // Get user from 'request'
        Optional<User> userFromRequest = this.manager.getUserFromContext(context);

        // Make sure we did not get the user (token expired)
        assertTrue(userFromRequest.isEmpty());

        // Set expiration time back to 15
        this.properties.setProperty("JWT_VALID_MIN", "15");

        // Generate refresh token
        String refreshToken = this.manager.generateRefreshToken(this.user);

        // Refresh our token
        token = this.manager.refreshToken(refreshToken, this.user.getUuid());

        // Make sure it was refreshed
        assertTrue(token.isPresent());

        // Try again
        context = this.createContextWithToken(token.get());
        userFromRequest = this.manager.getUserFromContext(context);

        // Make sure the correct user was returned after refreshing
        assertTrue(userFromRequest.isPresent());
        assertEquals(this.user.getUuid(), userFromRequest.get().getUuid());
    }

    @Test
    public void generateAndInvalidateRefreshToken() {
        // Add user and make sure it was added
        assertTrue(this.userStore.addUser(this.user, this.password, null).isPresent());

        // Generate refresh token
        String refreshToken = this.manager.generateRefreshToken(this.user);

        // Invalidate refresh token
        this.manager.invalidateRefreshToken(this.user);

        // Get a new token based on the refresh token
        Optional<String> token = this.manager.refreshToken(refreshToken, this.user.getUuid());

        // Make sure it was not generated
        assertTrue(token.isEmpty());
    }
}
