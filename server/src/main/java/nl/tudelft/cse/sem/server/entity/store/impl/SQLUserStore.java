package nl.tudelft.cse.sem.server.entity.store.impl;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.storage.database.HibernateUtil;
import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implements a user store with SQL.
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLUserStore implements UserStore {
    private static final Logger logger = LogManager.getLogger(SQLUserStore.class);
    private transient SessionFactory sessionFactory;

    @Override
    public void init(@NonNull Properties properties) {
        this.sessionFactory = HibernateUtil.getSessionFactory(properties);

        logger.info("Started SQL user store");
    }

    @Override
    public void teardown() {
        HibernateUtil.shutdown();
        logger.info("Stopped SQL user store");
    }

    protected Optional<DatabaseUser> loadUserFromID(Session session, UUID uuid) {
        logger.debug("Loading user with UUID " + uuid);
        return Optional.ofNullable(session.get(DatabaseUser.class, uuid));
    }

    @Override
    public Optional<User> loadUserFromID(@NonNull UUID uuid) {
        try (Session session = this.sessionFactory.openSession()) {
            return this.loadUserFromID(session, uuid).map(DatabaseUser::toUser);
        }
    }

    /*
     * PMD is warning about the hard-coded '1' in two if-statements in this code.
     * Normally this would be valid, but in this case the comments make it clear and
     * it should be easily understandable. Adding a variable 'one' with value '1'
     * would not improve quality here, it might even make it worse.
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    protected Optional<DatabaseUser> loadUserWithName(Session session, String username) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DatabaseUser> criteriaQuery = builder.createQuery(DatabaseUser.class);
        Root<DatabaseUser> userRoot = criteriaQuery.from(DatabaseUser.class);

        // Create query and log
        logger.debug("Loading user with name '" + username + "'");
        criteriaQuery.select(userRoot).where(builder.equal(userRoot.get("username"), username));

        // Get result
        List<DatabaseUser> users = session.createQuery(criteriaQuery).getResultList();

        // Only one user should be returned
        if (users.size() > 1) {
            logger.error("Query for username '" + username + "' returned " + users.size()
                + " users, even though it should only return one!");
        }

        // Get first (and hopefully the only) user from the list
        if (users.size() == 1) {
            logger.debug("Found user with name '" + username + "': " + users.get(0));
            return Optional.of(users.get(0));
        }

        logger.debug("No / too much user(s) found for username '" + username + "'");
        return Optional.empty();
    }

    @Override
    public Optional<User> loadUserFromName(@NonNull String loginName) {
        try (Session session = this.sessionFactory.openSession()) {
            return this.loadUserWithName(session, loginName).map(DatabaseUser::toUser);
        }
    }

    /*
     * In this case 'result' is a DD-anomaly, which is correct in way. But this is a valid
     * flow and will not lead to errors/bugs. Fixing the error is possible by using dummy code,
     * but that will decrease the readability (among other things) of this code.
     */
    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Optional<User> addUser(@NonNull User user, @NonNull String password,
                                  @Nullable String mfaToken) {
        Optional<User> result = Optional.empty();

        try (Session session = this.sessionFactory.openSession()) {
            logger.debug("Attempting to add user '" + user + "'");
            session.beginTransaction();

            Optional<DatabaseUser> retrievedUser =
                this.loadUserWithName(session, user.getLoginName());

            if (retrievedUser.isEmpty()) {
                DatabaseUser databaseUser = new DatabaseUser(user.getLoginName(),
                    user.getDisplayName(), password, mfaToken);

                session.save(databaseUser);

                result = Optional.of(databaseUser.toUser());

                logger.info("New user added: " + result.get());
            }

            session.getTransaction().commit();
        }

        return result;
    }

    @Override
    public boolean userExists(@NonNull String loginName) {
        logger.debug("Checking if user with name exists '" + loginName + "'");
        try (Session session = this.sessionFactory.openSession()) {
            Optional<DatabaseUser> retrievedUser =
                this.loadUserWithName(session, loginName);

            return retrievedUser.isPresent();
        }
    }

    @Override
    public boolean userExists(@NonNull UUID uuid) {
        logger.debug("Checking if user with uuid exists '" + uuid + "'");
        try (Session session = this.sessionFactory.openSession()) {
            Optional<DatabaseUser> retrievedUser =
                this.loadUserFromID(session, uuid);

            return retrievedUser.isPresent();
        }
    }

    @Override
    public Optional<User> removeUser(@NonNull UUID uuid) {
        Optional<User> result;

        try (Session session = this.sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<DatabaseUser> criteriaDelete = builder
                .createCriteriaDelete(DatabaseUser.class);
            Root<DatabaseUser> userRoot = criteriaDelete.from(DatabaseUser.class);

            // Creating query
            criteriaDelete.where(builder.equal(userRoot.get("uuid"), uuid));

            // Log and start transaction
            logger.info("Attempting to remove user with uuid " + uuid);
            session.beginTransaction();

            // Get user based on uuid
            result = this.loadUserFromID(session, uuid).map(DatabaseUser::toUser);

            // Delete user and commit transacting
            session.createQuery(criteriaDelete).executeUpdate();
            session.getTransaction().commit();
        }

        return result;
    }

    @Override
    public Optional<User> updateUser(@NonNull UUID uuid, @NonNull User user) {
        try (Session session = this.sessionFactory.openSession()) {
            logger.info("Attempting to update user with uuid '" + uuid + "' to " + user);
            Optional<DatabaseUser> databaseUser = loadUserFromID(session, user.getUuid());
            Optional<User> originalUser = databaseUser.map(DatabaseUser::toUser);

            // If the uuid does not match a known uuid or the uuid has changed,
            // return and empty optional
            if (databaseUser.isEmpty() || originalUser.isEmpty() || !user.getUuid().equals(uuid)) {
                logger.debug("Could not update user: UUID does not exist or has changed");
                return Optional.empty();
            }

            Optional<User> userWithName = this
                .loadUserWithName(session, user.getLoginName()).map(DatabaseUser::toUser);

            // If the username is taken, and its not by the current user,
            // someone else has taken the name
            if (userWithName.isPresent()
                && !userWithName.get().getUuid().equals(originalUser.get().getUuid())) {
                logger.debug("Could not update user: login name was taken");
                return Optional.empty();
            }

            session.beginTransaction();
            DatabaseUser data = databaseUser.get();

            // Update values
            data.setDisplayName(user.getDisplayName());
            data.setUsername(user.getLoginName());

            // Save updated values
            session.save(data);

            session.getTransaction().commit();
            logger.debug("Successfully updated user " + user);
        }

        return Optional.of(user);
    }

    @Override
    public boolean verifyPassword(@NonNull User user, @NonNull String password) {
        logger.debug("Verifying password '****' for user " + user);
        try (Session session = this.sessionFactory.openSession()) {
            Optional<DatabaseUser> databaseUser = this.loadUserFromID(session, user.getUuid());

            // If the user was found, just make sure the password was correct
            return databaseUser.isPresent() && databaseUser.get().verifyPassword(password);
        }
    }

    @Override
    public boolean verifyMultiFactorAuthenticationToken(@NonNull User user,
                                                        @Nullable String token) {
        logger.debug("Verifying MFA token '" + token + "' for user " + user);
        try (Session session = this.sessionFactory.openSession()) {
            Optional<DatabaseUser> databaseUser = this.loadUserFromID(session, user.getUuid());

            // If the user was found, just make sure the token was correct
            return databaseUser.isPresent() && databaseUser.get().verifyMfaToken(token);
        }
    }
}
