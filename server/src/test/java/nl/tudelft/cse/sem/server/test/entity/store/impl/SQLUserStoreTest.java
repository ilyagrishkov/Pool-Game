package nl.tudelft.cse.sem.server.test.entity.store.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.testing.NullPointerTester;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import nl.tudelft.cse.sem.server.Bootstrap;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.impl.SQLUserStore;
import org.hibernate.cfg.Environment;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLUserStoreTest {
    private transient SQLUserStore db;
    private transient String username = "ivar";
    private transient String password = "badPassword";
    private transient User user;

    @BeforeEach
    void before() {
        this.db = new SQLUserStore();
        this.user = User.builder()
            .uuid(UUID.randomUUID())
            .displayName("display_name")
            .loginName(this.username)
            .build();


        Properties properties = Bootstrap.getDefaultDatabaseProperties();

        // Get properties for test db
        properties.put(Environment.URL,
            "jdbc:mysql://" + System.getProperty("mysql.location")
                + System.getProperty("mysql.port")
                + "/" + System.getProperty("mysql.db")
                + "?autoReconnect=true&createDatabaseIfNotExist=true");
        properties.put(Environment.PASS, System.getProperty("mysql.pw"));
        properties.put(Environment.USER, System.getProperty("mysql.user"));
        properties.put(Environment.HBM2DDL_AUTO, "create");

        this.db.init(properties);
    }

    @AfterEach
    void after() {
        this.db.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.setDefault(User.class, this.user);
        tester.testAllPublicStaticMethods(SQLUserStore.class);
        tester.testAllPublicInstanceMethods(this.db);
    }

    @Test
    void testAddUser() {
        Optional<User> user = this.db.addUser(this.user, this.password, null);

        assertTrue(user.isPresent());
        assertTrue(db.verifyPassword(user.get(), this.password));
        assertTrue(this.db.userExists(this.username));
        assertTrue(this.db.userExists(user.get().getUuid()));
    }

    @Test
    void wrongPassword() {
        String wrongPassword = password + "wrong";
        Optional<User> user = this.db.addUser(this.user, this.password, null);

        assertTrue(user.isPresent());
        assertFalse(db.verifyPassword(user.get(), wrongPassword));
    }

    @Test
    void loginForNonExistingUser() {
        assertFalse(this.db.userExists(this.username));
        assertFalse(db.verifyPassword(this.user, this.password));
    }

    @Test
    void testDoubleUsername() {
        assertTrue(this.db.addUser(this.user, this.password, null).isPresent());
        assertTrue(this.db.addUser(this.user, this.password, null).isEmpty());
    }

    @Test
    void testDeleteUser() {
        Optional<User> user = this.db.addUser(this.user, this.password, null);

        assertTrue(user.isPresent());
        assertTrue(this.db.removeUser(user.get().getUuid()).isPresent());
        assertTrue(this.db.removeUser(user.get().getUuid()).isEmpty());
    }

    @Test
    void shouldReturnUserUsingUuid() {
        // Add user to DB
        this.db.addUser(this.user, this.password, null);

        // Retrieve it by name
        Optional<User> user = this.db.loadUserFromName(this.username);

        // Make sure the user was found
        assertTrue(user.isPresent());

        // Get the user object by using it's uuid
        Optional<User> userFromUuid = this.db.loadUserFromID(user.get().getUuid());

        // Make sure the user was found and make sure they are equal
        assertTrue(userFromUuid.isPresent());
        assertEquals(user, userFromUuid);
    }

    @Test
    public void shouldUpdateUser() {
        // Add user to DB
        this.db.addUser(this.user, this.password, null);

        // Retrieve it by name
        Optional<User> userByName = this.db.loadUserFromName(this.username);

        // Make sure the user was found
        assertTrue(userByName.isPresent());

        // Update the name
        User user = userByName.get();
        String name = "yeet";

        user.setLoginName(name);
        Optional<User> updatedUser = this.db.updateUser(user.getUuid(), user);

        // Make sure the user was updated
        assertTrue(updatedUser.isPresent());

        // Get the user object by using it's uuid
        Optional<User> userFromUuid = this.db.loadUserFromID(user.getUuid());

        // Make sure the user was found and make sure the name was updated
        assertTrue(userFromUuid.isPresent());
        assertEquals(name, userFromUuid.get().getLoginName());
    }

    @Test
    public void shouldUpdateUserWhenNoUpdate() {
        // Add user to DB
        this.db.addUser(this.user, this.password, null);

        // Retrieve it by name
        Optional<User> userByName = this.db.loadUserFromName(this.username);

        // Make sure the user was found
        assertTrue(userByName.isPresent());
        User user = userByName.get();
        Optional<User> updatedUser = this.db.updateUser(user.getUuid(), user);

        // Make sure the user was updated
        assertTrue(updatedUser.isPresent());

        // Get the user object by using it's uuid
        Optional<User> userFromUuid = this.db.loadUserFromID(user.getUuid());

        // Make sure the user was found and make sure the name was updated
        assertTrue(userFromUuid.isPresent());
    }

    @Test
    public void shouldNotUpdateWhenNoUser() {
        Optional<User> updatedUser = this.db.updateUser(user.getUuid(), user);

        assertTrue(updatedUser.isEmpty());
    }

    @Test
    public void shouldNotUpdateWhenDifferentUUID() {
        // Add user to DB
        this.db.addUser(this.user, this.password, null);

        Optional<User> updatedUser = this.db.updateUser(UUID.randomUUID(), user);

        assertTrue(updatedUser.isEmpty());
    }

    @Test
    public void shouldNotUpdateWhenNameTaken() {
        // Add user to DB
        this.db.addUser(this.user, this.password, null);

        String name = "yeet2";
        User otherUser = User.builder()
            .displayName("awdaw").loginName(name).uuid(UUID.randomUUID()).build();
        this.db.addUser(otherUser, this.password, null);

        // Retrieve it by name
        Optional<User> userByName = this.db.loadUserFromName(this.username);

        // Make sure the user was found
        assertTrue(userByName.isPresent());

        // Update the name
        User user = userByName.get();

        user.setLoginName(name);
        Optional<User> updatedUser = this.db.updateUser(user.getUuid(), user);

        assertTrue(updatedUser.isEmpty());
    }

    @Test
    void shouldReturnOkIfNoMultiFactor() {
        Optional<User> user = this.db.addUser(this.user, this.password, null);

        assertTrue(user.isPresent());
        assertTrue(db.verifyMultiFactorAuthenticationToken(user.get(), null));
    }

    @Test
    void shouldReturnOkIfCorrectMultiFactor() {
        String mfa = Base32.random();
        Optional<User> user = this.db.addUser(this.user, this.password, mfa);

        assertTrue(user.isPresent());
        assertTrue(db.verifyMultiFactorAuthenticationToken(user.get(), new Totp(mfa).now()));
    }

    @Test
    void shouldReturnWrongIfIncorrectMultiFactor() {
        String mfa = Base32.random();
        String token = "123456";

        if (new Totp(mfa).now().equals(token)) {
            token = "122456";
        }

        Optional<User> user = this.db.addUser(this.user, this.password, mfa);

        assertTrue(user.isPresent());
        assertFalse(db.verifyMultiFactorAuthenticationToken(user.get(), token));
    }

    @Test
    void shouldReturnWrongIfNoUser() {
        assertFalse(db.verifyMultiFactorAuthenticationToken(this.user, "412610"));
    }
}
