package nl.tudelft.cse.sem.shared.test.entity.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.cse.sem.shared.entity.auth.Credentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CredentialsTest {
    private transient String username;
    private transient String password;
    private transient String empty;

    /**
     * Sets up the dependencies.
     */
    @BeforeEach
    public void setUp() {
        this.username = "test";
        this.password = this.username;
        this.empty = "";
    }

    @Test
    public void shouldReturnTrueIfCorrect() {
        Credentials credentials = Credentials.builder()
            .username(this.username)
            .password(this.password)
            .build();

        assertTrue(credentials.correct());
    }

    @Test
    public void shouldReturnFalseIfNoUsername() {
        Credentials credentials = Credentials.builder()
            .username(this.empty)
            .password(this.password)
            .build();

        assertFalse(credentials.correct());
    }

    @Test
    public void shouldReturnFalseIfNoPassword() {
        Credentials credentials = Credentials.builder()
            .username(this.username)
            .password(this.empty)
            .build();

        assertFalse(credentials.correct());
    }

    @Test
    public void shouldReturnFalseIfNothing() {
        Credentials credentials = Credentials.builder()
            .username(this.empty)
            .password(this.empty)
            .build();

        assertFalse(credentials.correct());
    }
}
