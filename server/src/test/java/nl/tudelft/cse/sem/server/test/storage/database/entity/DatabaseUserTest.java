package nl.tudelft.cse.sem.server.test.storage.database.entity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseUser;
import org.junit.jupiter.api.Test;

class DatabaseUserTest {
    DatabaseUser createTestUser() {
        return new DatabaseUser("ivardb", "", "badPassword", null);
    }

    @Test
    void validatePasswordTest() {
        DatabaseUser user = createTestUser();
        assertTrue(user.verifyPassword("badPassword"));
    }

    @Test
    public void shouldThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> createTestUser().verifyPassword(null));
    }
}
