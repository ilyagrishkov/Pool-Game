package nl.tudelft.cse.sem.server.test.storage.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.testing.NullPointerTester;
import java.util.Properties;
import nl.tudelft.cse.sem.server.storage.cache.InMemoryCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryCacheTest {
    private transient Properties properties;
    private transient InMemoryCache cache;

    /**
     * Sets up dependencies and cache.
     */
    @BeforeEach
    public void setUp() {
        this.properties = new Properties();
        this.cache = new InMemoryCache();

        this.cache.init(this.properties);
    }

    @AfterEach
    public void tearDown() {
        this.cache.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.testAllPublicStaticMethods(InMemoryCache.class);
        tester.testAllPublicInstanceMethods(this.cache);
    }

    @Test
    public void shouldCreateInMemoryCache() {
        assertNotNull(this.cache.getTokenStore());
    }
}
