package nl.tudelft.cse.sem.server.test.entity.store.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.testing.NullPointerTester;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.impl.InMemoryUserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryUserStoreTest {
    private transient InMemoryUserStore userStore;
    private transient UUID uuid;
    private transient User user;
    private transient String password;

    @BeforeEach
    private void setUp() {
        this.userStore = new InMemoryUserStore();
        this.uuid = UUID.randomUUID();
        this.password = "password";
        this.user = User.builder()
            .uuid(uuid)
            .displayName("display_name")
            .loginName("login_name")
            .build();

        this.userStore.init(new Properties());
    }

    @AfterEach
    private void tearDown() {
        this.userStore.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.setDefault(User.class, this.user);
        tester.testAllPublicStaticMethods(InMemoryUserStore.class);
        tester.testAllPublicInstanceMethods(this.userStore);
    }

    @Test
    public void shouldAddUserIfNotExists() {
        // Add user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it was added
        assertTrue(addedUser.isPresent());
        assertTrue(this.userStore.userExists(this.user.getLoginName()));
        assertTrue(this.userStore.userExists(this.uuid));
    }

    @Test
    public void shouldNotAddUserIfDuplicate() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Add it again
        Optional<User> secondAdd = this.userStore.addUser(this.user.toBuilder().build(),
            this.password, null);

        // Make sure it wasn't added for the second time because of name conflict
        assertTrue(secondAdd.isEmpty());
    }

    @Test
    public void shouldNotAddUserIfNameExists() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Modify uuid
        User user = this.user.toBuilder().uuid(UUID.randomUUID()).build();

        // Add it again
        Optional<User> secondAdd = this.userStore.addUser(user, this.password, null);

        // Make sure it wasn't added for the second time because of name conflict
        assertTrue(secondAdd.isEmpty());
    }

    @Test
    public void shouldNotAddUserIfUuidExists() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Modify name
        User user = this.user.toBuilder().loginName(UUID.randomUUID().toString()).build();

        // Add it again
        Optional<User> secondAdd = this.userStore.addUser(user, this.password, null);

        // Make sure it wasn't added for the second time because of uuid conflict
        assertTrue(secondAdd.isEmpty());
    }

    @Test
    public void shouldNotAddUSerIfNoNames() {
        // Change user
        this.user.setLoginName(null);
        this.user.setDisplayName(null);

        // Add the user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it wasn't added because the names were null
        assertTrue(addedUser.isEmpty());
    }

    @Test
    public void shouldNotAddUSerIfNoLoginName() {
        // Change user
        this.user.setLoginName(null);

        // Add the user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it wasn't added because the login name was null
        assertTrue(addedUser.isEmpty());
    }

    @Test
    public void shouldNotAddUSerIfNoDisplayName() {
        // Change user
        this.user.setDisplayName(null);

        // Add the user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it wasn't added because the display name was null
        assertTrue(addedUser.isEmpty());
    }

    @Test
    public void shouldRemoveUser() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Remove user
        this.userStore.removeUser(this.uuid);

        // Make sure the user was removed
        assertFalse(this.userStore.userExists(this.user.getLoginName()));
        assertFalse(this.userStore.userExists(this.uuid));
    }

    @Test
    public void shouldUpdateUserIfNoConflict() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Change login name
        User user = this.user.toBuilder()
            .loginName(this.user.getLoginName().toUpperCase(Locale.getDefault())).build();

        // Update the user
        this.userStore.updateUser(this.uuid, user);

        // Make sure the user was updated
        assertTrue(this.userStore.userExists(user.getLoginName()));
        assertFalse(this.userStore.userExists(this.user.getLoginName()));
    }

    @Test
    public void shouldNotUpdateIfUserNotExists() {
        // Update the user
        Optional<User> updatedUser = this.userStore.updateUser(this.uuid, this.user);

        // Make sure the optional is empty (and therefore nothing was added/updated)
        assertTrue(updatedUser.isEmpty());
    }

    @Test
    public void shouldNotUpdateIfUuidChanged() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Change uuid
        User user = this.user.toBuilder().uuid(UUID.randomUUID()).build();

        // Update the user
        Optional<User> updatedUser = this.userStore.updateUser(this.uuid, user);

        // Make sure the optional is empty (and therefore nothing was added/updated)
        assertTrue(updatedUser.isEmpty());
    }

    @Test
    public void shouldNotUpdateIfNameConflict() {
        // Add user
        this.userStore.addUser(this.user, this.password, null);

        // Add another user and add it to the store
        this.userStore.addUser(User.builder()
            .uuid(UUID.randomUUID())
            .loginName(this.user.getLoginName().toUpperCase(Locale.getDefault()))
            .displayName(this.user.getDisplayName().toUpperCase(Locale.getDefault()))
            .build(), this.password, null);

        // Change login name
        User user = this.user.toBuilder()
            .loginName(this.user.getLoginName().toUpperCase(Locale.getDefault())).build();

        // Attempt to update
        Optional<User> updatedUser = this.userStore.updateUser(this.uuid, user);

        // Make sure the optional is empty (and therefore nothing was added/updated)
        assertTrue(updatedUser.isEmpty());
    }

    @Test
    public void shouldReturnTrue() {
        assertTrue(this.userStore.verifyMultiFactorAuthenticationToken(this.user, null));
    }

    @Test
    public void shouldReturnTrueIfCorrectPassword() {
        // Add user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it was added
        assertTrue(addedUser.isPresent());
        assertTrue(this.userStore.verifyPassword(user, this.password));
    }

    @Test
    public void shouldReturnFalseIfIncorrectPassword() {
        // Add user
        Optional<User> addedUser = this.userStore.addUser(this.user, this.password, null);

        // Make sure it was added
        assertTrue(addedUser.isPresent());
        assertFalse(this.userStore.verifyPassword(user, "123" + this.password));
    }

    @Test
    public void shouldReturnFalseIfNotExists() {
        assertFalse(this.userStore.verifyPassword(user, this.password));
    }
}
