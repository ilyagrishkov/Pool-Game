package nl.tudelft.cse.sem.server.entity.store;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;

/**
 * Interface for the user store. This should handle all user-related tasks.
 */
public interface UserStore {
    /**
     * Prepares the user store for use. Things like DB connections happen here.
     *
     * @param properties {@link Properties} properties to use
     */
    void init(@NonNull Properties properties);

    /**
     * Gives the user store a chance to gracefully shutdown.
     */
    void teardown();

    /**
     * Returns an {@link Optional} containing the user object, iff it was in the store,
     * based on the given uuid.
     *
     * @param uuid The user's {@link UUID}
     * @return An {@link Optional} which contains the user associated
     *     with the given {@link UUID}, or an empty one if the {@link UUID} was unknown.
     */
    Optional<User> loadUserFromID(@NonNull UUID uuid);

    /**
     * Returns an {@link Optional} containing the user object, iff it was in the store,
     * based on the given login name.
     *
     * @param loginName A {@link String} representing the user's name
     * @return An {@link Optional} which contains the user associated
     *     with the given login name, or an empty one if the login name was unknown.
     */
    Optional<User> loadUserFromName(@NonNull String loginName);

    /**
     * Adds an user to the store, and returns an {@link Optional}
     * containing the object which is stored in memory.
     *
     * @param user                           The {@link User} object to store
     * @param password                       The user's password
     * @param multiFactorAuthenticationToken The MFA token
     * @return An {@link Optional} containing the object which is stored in memory,
     *     or an empty one if it could not be stored.
     */
    Optional<User> addUser(@NonNull User user, @NonNull String password,
                           @Nullable String multiFactorAuthenticationToken);

    /**
     * Returns a boolean indicating if the login name is taken.
     *
     * @param loginName A {@link String} representing the chosen username
     * @return True if the login name exists, false if not.
     */
    boolean userExists(@NonNull String loginName);

    /**
     * Returns a boolean indicating if the uuid is taken.
     *
     * @param uuid An {@link UUID} to check
     * @return True if the uuid exists, false if not.
     */
    boolean userExists(@NonNull UUID uuid);

    /**
     * Deletes the given user from the store based on the given uuid
     * and returns an optional containing the removed object.
     *
     * @param uuid The user's {@link UUID}
     * @return An {@link Optional} which contains the removed user,
     *     or empty if the uuid did not match with any user.
     */
    Optional<User> removeUser(@NonNull UUID uuid);

    /**
     * Updates the user object in the store with the given uuid to match the given user object.
     *
     * @param uuid The user's {@link UUID}
     * @param user The updated {@link User}
     * @return An {@link Optional} containing the updated {@link User},
     *     or empty if the given uuid did not match any user or the updated object
     *     conflicts (uuid cannot change, duplicate login name, ...) with existing objects.
     */
    Optional<User> updateUser(@NonNull UUID uuid, @NonNull User user);

    /**
     * Verifies the given password and returns a boolean indicating success or failure.
     *
     * @param user     The {@link User} which should be used to verifiy the password
     * @param password The {@link String} representing the password
     * @return A boolean that is true when the given password is correct, and false otherwise.
     */
    boolean verifyPassword(@NonNull User user, @NonNull String password);

    /**
     * Verifies the given token and returns a boolean indicating success or failure.
     *
     * @param user  The {@link User} which should be used to verify the token
     * @param token The {@link String} representing the token
     * @return A boolean that is true when the given token is correct, and false otherwise.
     */
    boolean verifyMultiFactorAuthenticationToken(@NonNull User user, String token);
}
