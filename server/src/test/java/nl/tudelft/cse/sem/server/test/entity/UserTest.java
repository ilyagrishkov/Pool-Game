package nl.tudelft.cse.sem.server.test.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.testing.NullPointerTester;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nl.tudelft.cse.sem.server.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UserTest {
    @Captor
    private transient ArgumentCaptor<String> nameCaptor;
    @Captor
    private transient ArgumentCaptor<String> valueCaptor;
    private static UUID genericUUID;

    private static final String LOGIN_NAME = "login_name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String UUID_NAME = "uuid";
    private static final String ROLE_NAME = "role";
    private static final String LOGIN_NAME_VAL = "test";
    private static final String DISPLAY_NAME_VAL = "testd";

    @BeforeAll
    public static void setUpAll() {
        genericUUID = UUID.randomUUID();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(User.class);
        tester.testAllPublicInstanceMethods(User
            .builder().uuid(UUID.randomUUID())
            .build());
    }

    @Test
    public void shouldAddAllValues() {
        // Create test user
        User user = User.builder()
            .loginName(LOGIN_NAME_VAL)
            .displayName(DISPLAY_NAME_VAL)
            .uuid(genericUUID)
            .build();

        // Create test builder
        JWTCreator.Builder builder = Mockito.mock(JWTCreator.Builder.class);
        when(builder.withClaim(anyString(), anyString())).thenReturn(builder);

        // Save user to builder
        user.addToToken(builder);

        // Verify the method was called 4 times
        verify(builder, times(3))
            .withClaim(nameCaptor.capture(), valueCaptor.capture());

        // Assert that all keys are equal to their expected value
        assertEquals(LOGIN_NAME, nameCaptor.getAllValues().get(0));
        assertEquals(DISPLAY_NAME, nameCaptor.getAllValues().get(1));
        assertEquals(UUID_NAME, nameCaptor.getAllValues().get(2));

        // Idem for expected values
        assertEquals(LOGIN_NAME_VAL, valueCaptor.getAllValues().get(0));
        assertEquals(DISPLAY_NAME_VAL, valueCaptor.getAllValues().get(1));
        assertEquals(genericUUID.toString(), valueCaptor.getAllValues().get(2));
    }

    /**
     * Helper method to create a decoded token.
     *
     * @param map Map with claims
     * @return A decoded token
     */
    // If only PMD UR anomaly wasn't so incapable..
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public static DecodedJWT buildMock(Map<String, String> map) {
        DecodedJWT decodedToken = Mockito.mock(DecodedJWT.class);

        for (String name : map.keySet()) {
            Claim claim = Mockito.mock(Claim.class);

            when(claim.asString()).thenReturn(map.get(name));
            when(decodedToken.getClaim(eq(name))).thenReturn(claim);
        }

        return decodedToken;
    }

    @Test
    public void shouldReturnFullUser() {
        // Create map and decoded token
        Map<String, String> tokenMap = new HashMap<>();

        tokenMap.put(LOGIN_NAME, LOGIN_NAME_VAL);
        tokenMap.put(DISPLAY_NAME, DISPLAY_NAME_VAL);
        tokenMap.put(UUID_NAME, genericUUID.toString());

        DecodedJWT decodedToken = buildMock(tokenMap);

        // Get user from token
        User user = User.buildFromToken(decodedToken);

        // Assert that the created user matches the one specified in the claims
        assertEquals(LOGIN_NAME_VAL, user.getLoginName());
        assertEquals(DISPLAY_NAME_VAL, user.getDisplayName());
        assertEquals(genericUUID, user.getUuid());
    }

    @Test
    public void shouldReturnRandomUuidUser() {
        // Create map and decoded token
        Map<String, String> tokenMap = new HashMap<>();

        tokenMap.put(LOGIN_NAME, LOGIN_NAME_VAL);
        tokenMap.put(DISPLAY_NAME, DISPLAY_NAME_VAL);
        tokenMap.put(UUID_NAME, null);

        DecodedJWT decodedToken = buildMock(tokenMap);

        // Get user from token
        User user = User.buildFromToken(decodedToken);

        // Assert that the created user matches the one specified in the claims
        assertEquals(LOGIN_NAME_VAL, user.getLoginName());
        assertEquals(DISPLAY_NAME_VAL, user.getDisplayName());
        assertNotEquals(genericUUID, user.getUuid());
        assertNotNull(user.getUuid());
    }

    @Test
    public void shouldReturnUserWithoutPerms() {
        // Create map and decoded token
        Map<String, String> tokenMap = new HashMap<>();

        tokenMap.put(LOGIN_NAME, LOGIN_NAME_VAL);
        tokenMap.put(DISPLAY_NAME, DISPLAY_NAME_VAL);
        tokenMap.put(UUID_NAME, genericUUID.toString());
        tokenMap.put(ROLE_NAME, null);

        DecodedJWT decodedToken = buildMock(tokenMap);

        // Get user from token
        User user = User.buildFromToken(decodedToken);

        // Assert that the created user matches the one specified in the claims
        assertEquals(LOGIN_NAME_VAL, user.getLoginName());
        assertEquals(DISPLAY_NAME_VAL, user.getDisplayName());
        assertEquals(genericUUID, user.getUuid());
    }
}
