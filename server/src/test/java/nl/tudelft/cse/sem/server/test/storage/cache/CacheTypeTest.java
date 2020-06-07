package nl.tudelft.cse.sem.server.test.storage.cache;

import static org.junit.Assert.assertTrue;

import nl.tudelft.cse.sem.server.storage.cache.CacheType;
import nl.tudelft.cse.sem.server.storage.cache.InMemoryCache;
import org.junit.jupiter.api.Test;

public class CacheTypeTest {
    @Test
    public void shouldCreateMemoryCache() {
        assertTrue(CacheType.MEMORY.create() instanceof InMemoryCache);
    }
}
