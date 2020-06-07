package nl.tudelft.cse.sem.server.test.storage.database;

import static org.junit.Assert.assertTrue;

import nl.tudelft.cse.sem.server.storage.database.DatabaseType;
import nl.tudelft.cse.sem.server.storage.database.SQLDatabase;
import org.junit.jupiter.api.Test;

public class DatabaseTypeTest {
    @SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL
    @Test
    public void shouldReturnSQLDatabaseInstance() {
        assertTrue(DatabaseType.SQL.create() instanceof SQLDatabase);
    }
}
