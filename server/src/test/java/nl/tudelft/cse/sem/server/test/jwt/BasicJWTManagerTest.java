package nl.tudelft.cse.sem.server.test.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.testing.NullPointerTester;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.jwt.BasicJWTManager;
import nl.tudelft.cse.sem.server.jwt.JWTProvider;
import nl.tudelft.cse.sem.server.jwt.store.RefreshTokenStore;
import nl.tudelft.cse.sem.server.test.entity.UserTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about JWT being in there
public class BasicJWTManagerTest {
    private transient BasicJWTManager manager;
    private transient UserStore userStore;
    private transient RefreshTokenStore tokenStore;
    private transient JWTProvider provider;
    private transient User user;

    private transient HttpServletRequest request;
    private transient HttpServletResponse response;
    private transient Context context;

    private static final String AUTH_HEADER = "Authorization";
    private static final String PMD_IS_DUMB = "token";

    @Captor
    private transient ArgumentCaptor<String> tokenCaptor;

    /**
     * Sets up dependencies and manager.
     */
    @BeforeEach
    public void setUp() {
        this.userStore = Mockito.mock(UserStore.class);
        this.tokenStore = Mockito.mock(RefreshTokenStore.class);
        this.provider = Mockito.mock(JWTProvider.class);
        this.user = Mockito.mock(User.class);
        this.request = Mockito.mock(HttpServletRequest.class);
        this.response = Mockito.mock(HttpServletResponse.class);
        this.context = ContextUtil.init(this.request, this.response);

        this.manager = BasicJWTManager.builder()
            .jwtProvider(this.provider)
            .tokenStore(this.tokenStore)
            .userStore(this.userStore)
            .properties(new Properties())
            .build();

        this.manager.init();

        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void tearDown() {
        this.manager.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(BasicJWTManager.class);
        tester.testAllPublicInstanceMethods(manager);
    }

    @Test
    public void shouldInitTokenStoreOnInit() {
        verify(this.tokenStore, times(1)).init(any());
    }

    @Test
    public void shouldAddUserClaimsWhenGenerating() {
        // Call generate function
        this.manager.generateToken(this.user);

        // Verify the user had to add its claims
        verify(this.user, times(1)).addToToken(any());
    }

    @Test
    public void shouldCreateAndStoreRefreshToken() {
        // Call generate function
        String token = this.manager.generateRefreshToken(this.user);

        // Verify the store was called
        verify(this.tokenStore, times(1))
            .addRefreshToken(tokenCaptor.capture(), any());

        // Make sure the correct token was stored
        assertEquals(token, tokenCaptor.getValue());
    }

    @Test
    public void shouldCallTokenStoreToRemoveToken() {
        // Create uuid
        UUID uuid = UUID.randomUUID();
        when(this.user.getUuid()).thenReturn(uuid);

        // Call invalidate function
        this.manager.invalidateRefreshToken(this.user);

        // Verify the store was called with the correct uuid
        verify(this.tokenStore, times(1)).removeRefreshToken(eq(uuid));
    }

    @Test
    public void shouldRefreshTokenIfCorrectRefreshToken() {
        // Create uuid and token
        UUID uuid = UUID.randomUUID();
        String token = "token";

        // Setup dependencies
        when(this.tokenStore.correctRefreshToken(anyString(), eq(uuid))).thenReturn(true);
        when(this.userStore.loadUserFromID(eq(uuid))).thenReturn(Optional.of(this.user));
        when(this.provider.signToken(any())).thenReturn(Optional.of(token));

        // Call function
        Optional<String> refreshedToken = this.manager.refreshToken(token, uuid);

        // Verify user had to add its claims
        verify(this.user, times(1)).addToToken(any());

        // Make sure the token was generated
        assertTrue(refreshedToken.isPresent());
        assertEquals(token, refreshedToken.get());
    }

    @Test
    public void shouldNotRefreshTokenIfInvalidRefreshToken() {
        // Setup dependencies
        when(this.tokenStore.correctRefreshToken(anyString(), any())).thenReturn(false);

        // Call function
        Optional<String> refreshedToken = this.manager
            .refreshToken(PMD_IS_DUMB, UUID.randomUUID());

        // Make sure the optional is empty
        assertTrue(refreshedToken.isEmpty());
    }

    @Test
    public void shouldNotRefreshTokenIfUserDeleted() {
        // Create uuid
        UUID uuid = UUID.randomUUID();

        // Setup dependencies
        when(this.tokenStore.correctRefreshToken(anyString(), eq(uuid))).thenReturn(true);
        when(this.userStore.loadUserFromID(eq(uuid))).thenReturn(Optional.empty());
        // Call function
        Optional<String> refreshedToken = this.manager
            .refreshToken(PMD_IS_DUMB, uuid);

        // Make sure the optional is empty
        assertTrue(refreshedToken.isEmpty());
    }

    @Test
    public void shouldReturnUserIfInAuthHeaders() {
        // Create uuid
        UUID uuid = UUID.randomUUID();

        // Create decoded token
        Map<String, String> userDetails = new HashMap<>();

        userDetails.put("login_name", null);
        userDetails.put("display_name", null);
        userDetails.put("uuid", uuid.toString());
        DecodedJWT decodedToken = UserTest.buildMock(userDetails);

        // Setup dependencies
        String header = "Bearer " + PMD_IS_DUMB;
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(header);
        when(this.provider.decodeToken(eq(PMD_IS_DUMB))).thenReturn(Optional.of(decodedToken));
        when(this.userStore.userExists(any(UUID.class))).thenReturn(true);

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user was built correctly
        assertTrue(user.isPresent());
        assertEquals(uuid, user.get().getUuid());
    }

    @Test
    public void shouldNotReturnUserIfShortHeaders() {
        // Create header
        String header = "Bearer";

        // Setup dependencies
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(header);

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user not returned
        assertTrue(user.isEmpty());
    }

    @Test
    public void shouldNotReturnUserIfIllegalHeaders() {
        // Create header
        String header = "Basic very cool token";

        // Setup dependencies
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(header);

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user not returned
        assertTrue(user.isEmpty());
    }

    @Test
    public void shouldNotReturnUserIfOtherHeaderScheme() {
        // Create header
        String header = "Basic token";

        // Setup dependencies
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(header);

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user not returned
        assertTrue(user.isEmpty());
    }

    @Test
    public void shouldNotReturnUserIfNoHeader() {
        // Setup dependencies
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(null);

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user not returned
        assertTrue(user.isEmpty());
    }

    @Test
    public void shouldNotReturnUserIfIncorrectToken() {
        // Create header
        String header = "Bearer " + PMD_IS_DUMB;

        // Setup dependencies
        when(this.request.getHeader(eq(AUTH_HEADER))).thenReturn(header);
        when(this.provider.decodeToken(eq(PMD_IS_DUMB))).thenReturn(Optional.empty());

        // Call function
        Optional<User> user = this.manager.getUserFromContext(this.context);

        // Make sure the user not returned
        assertTrue(user.isEmpty());
    }
}
