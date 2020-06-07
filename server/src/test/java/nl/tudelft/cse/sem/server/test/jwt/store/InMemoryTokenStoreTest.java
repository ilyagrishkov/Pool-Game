package nl.tudelft.cse.sem.server.test.jwt.store;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.testing.NullPointerTester;
import java.util.Properties;
import java.util.UUID;
import nl.tudelft.cse.sem.server.jwt.store.InMemoryTokenStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTokenStoreTest {
    private transient InMemoryTokenStore tokenStore;
    private static UUID uuid = UUID.randomUUID();
    private static String token = "coolToken";

    @BeforeEach
    private void setUp() {
        this.tokenStore = new InMemoryTokenStore();

        this.tokenStore.init(new Properties());
    }

    @AfterEach
    private void tearDown() {
        this.tokenStore.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(InMemoryTokenStore.class);
        tester.testAllPublicInstanceMethods(this.tokenStore);
    }

    @Test
    public void shouldAddTokenIfNotExists() {
        // Add to store
        this.tokenStore.addRefreshToken(token, uuid);

        // Make sure it was added
        assertTrue(this.tokenStore.correctRefreshToken(token, uuid));
    }

    @Test
    public void shouldRemoveToken() {
        // Add to store and remove it
        this.tokenStore.addRefreshToken(token, uuid);
        this.tokenStore.removeRefreshToken(uuid);

        // Make sure it was removed
        assertFalse(this.tokenStore.correctRefreshToken(token, uuid));
    }

    @Test
    public void shouldReturnFalseIfUuidNotExists() {
        // Make sure it returns false if the uuid is unknown
        assertFalse(this.tokenStore.correctRefreshToken(token, uuid));
    }

    @Test
    public void shouldReturnFalseIfWrongToken() {
        // Add to store
        this.tokenStore.addRefreshToken(token, uuid);

        // Make sure it returns false if the token is wrong
        assertFalse(this.tokenStore.correctRefreshToken("not" + token, uuid));
    }
}
