package nl.tudelft.cse.sem.shared.test.entity.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.cse.sem.shared.entity.auth.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegistrationTest {
    private transient String username;
    private transient String password;
    private transient String displayName;
    private transient String empty;

    /**
     * Sets up the dependencies.
     */
    @BeforeEach
    public void setUp() {
        this.username = "test";
        this.password = this.username;
        this.displayName = this.username;
        this.empty = "";
    }

    @Test
    public void shouldReturnTrueIfCorrect() {
        Registration registration = Registration.builder()
            .username(this.username)
            .password(this.password)
            .displayName(this.displayName)
            .build();

        assertTrue(registration.correct());
    }

    @Test
    public void shouldReturnFalseIfNoDisplayName() {
        Registration registration = Registration.builder()
            .username(this.username)
            .password(this.password)
            .displayName(this.empty)
            .build();

        assertFalse(registration.correct());
    }

    @Test
    public void shouldReturnFalseIfNothing() {
        Registration registration = Registration.builder()
            .username(this.username)
            .password(this.empty)
            .displayName(this.empty)
            .build();

        assertFalse(registration.correct());
    }
}
