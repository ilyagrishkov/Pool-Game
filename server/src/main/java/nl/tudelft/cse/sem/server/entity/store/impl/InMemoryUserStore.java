package nl.tudelft.cse.sem.server.entity.store.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple implementation of {@link UserStore} which stores users in memory.
 */
public class InMemoryUserStore implements UserStore {
    private static final Logger logger = LogManager.getLogger(InMemoryUserStore.class);
    private transient Map<UUID, User> users;
    private transient Map<UUID, String> passwords;

    @Override
    public void init(@NonNull Properties properties) {
        logger.debug("Creating map to store users");
        this.users = new HashMap<>();
        this.passwords = new HashMap<>();
    }

    @Override
    public void teardown() {
        logger.info("User store has stopped");
    }

    @Override
    public Optional<User> loadUserFromID(@NonNull UUID uuid) {
        logger.debug("Attempting to load user with uuid " + uuid);
        return Optional.ofNullable(this.users.get(uuid));
    }

    @Override
    public Optional<User> loadUserFromName(@NonNull String loginName) {
        logger.debug("Attempting to load user with name " + loginName);
        return this.users.values().stream()
            .filter(user -> user.getLoginName().equals(loginName)).findFirst();
    }

    @Override
    public Optional<User> addUser(@NonNull User user, @NonNull String password,
                                  @Nullable String mfaToken) {
        logger.info("Attempting to create a new user " + user);

        // If user exists or any of the names are null return empty optional
        if (user.getDisplayName() == null || user.getLoginName() == null
            || this.userExists(user.getLoginName()) || this.userExists(user.getUuid())) {
            logger.debug("Could not create user " + user);
            return Optional.empty();
        }

        logger.debug("Created user " + user);
        this.users.put(user.getUuid(), user);
        this.passwords.put(user.getUuid(), password);
        return Optional.of(user);
    }

    @Override
    public boolean userExists(@NonNull String loginName) {
        logger.debug("Checking if user with name '" + loginName + "' exists");
        return this.users.values().stream()
            .anyMatch(user -> user.getLoginName().equals(loginName));
    }

    @Override
    public boolean userExists(@NonNull UUID uuid) {
        logger.debug("Checking if user with uuid '" + uuid + "' exists");
        return this.users.containsKey(uuid);
    }

    @Override
    public Optional<User> removeUser(@NonNull UUID uuid) {
        logger.info("Removing user with uuid " + uuid);
        return Optional.ofNullable(this.users.remove(uuid));
    }

    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Optional<User> updateUser(@NonNull UUID uuid, @NonNull User user) {
        logger.info("Attempting to update user with uuid '" + uuid + "' to " + user);
        Optional<User> userWithName = this.loadUserFromName(user.getLoginName());
        Optional<User> originalUser = this.loadUserFromID(uuid);

        // If the uuid does not match a known uuid or the uuid has changed,
        // return and empty optional
        if (originalUser.isEmpty() || !user.getUuid().equals(uuid)) {
            logger.debug("Could not update user: UUID does not exist or has changed");
            return Optional.empty();
        }

        // If the username is taken, and its not by the current user,
        // someone else has taken the name
        if (userWithName.isPresent()
            && !userWithName.get().getUuid().equals(originalUser.get().getUuid())) {
            logger.debug("Could not update user: login name was taken");
            return Optional.empty();
        }

        logger.debug("Successfully updated user " + user);
        return Optional.ofNullable(this.users.put(uuid, user));
    }

    @Override
    public boolean verifyPassword(@NonNull User user, @NonNull String password) {
        return passwords.containsKey(user.getUuid())
            && passwords.get(user.getUuid()).equals(password);
    }

    @Override
    public boolean verifyMultiFactorAuthenticationToken(@NonNull User user,
                                                        @Nullable String token) {
        // Not supported for InMemoryUserStore for now
        return true;
    }
}
