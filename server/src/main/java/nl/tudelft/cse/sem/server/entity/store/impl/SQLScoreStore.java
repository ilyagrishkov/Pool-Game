package nl.tudelft.cse.sem.server.entity.store.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.storage.database.HibernateUtil;
import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseScore;
import nl.tudelft.cse.sem.server.storage.database.entity.DatabaseUser;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implements a score store with SQL.
 * Warning: This store depends on the fact that the {@link UserStore} being used is also a
 * SQL-based implementation.
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLScoreStore implements ScoreStore {
    private static final Logger logger = LogManager.getLogger(SQLScoreStore.class);

    private transient SessionFactory sessionFactory;
    private transient SQLUserStore userStore;

    @Override
    public void init(@NonNull Properties properties, @NonNull UserStore userStore) {
        this.sessionFactory = HibernateUtil.getSessionFactory(properties);
        this.userStore = (SQLUserStore) userStore;

        logger.info("Started SQL score store");
    }

    @Override
    public void teardown() {
        HibernateUtil.shutdown();
        logger.info("Stopped SQL score store");
    }

    /*
     * In this case 'result' is a DD-anomaly, which is correct in way. But this is a valid
     * flow and will not lead to errors/bugs. Fixing the error is possible by using dummy code,
     * but that will decrease the readability (among other things) of this code.
     */
    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Optional<Score> submitScore(@NonNull User user, int score) {
        Optional<Score> result = Optional.empty();

        logger.info("Submitting new score for user " + user + ": " + score);

        try (Session session = this.sessionFactory.openSession()) {
            session.beginTransaction();

            Optional<DatabaseUser> optionalUser = this.userStore
                .loadUserFromID(session, user.getUuid());

            if (optionalUser.isPresent()) {
                DatabaseUser retrievedUser = optionalUser.get();
                DatabaseScore databaseScore = new DatabaseScore(retrievedUser.getDisplayName(),
                    retrievedUser, score);

                // Save
                session.save(databaseScore);

                // Set result
                result = Optional.of(databaseScore.toScore());

                // Log
                logger.debug("Saved score for user " + retrievedUser + ": ");
            }

            session.getTransaction().commit();
        }

        return result;
    }

    @Override
    public List<Score> getScoresForUser(@NonNull User user) {
        try (Session session = this.sessionFactory.openSession()) {
            List<Score> scores = new ArrayList<>();

            // Load user and add scores to result if found
            this.userStore.loadUserFromID(session, user.getUuid())
                .ifPresent(value -> scores.addAll(value.getScores()
                    .stream().map(DatabaseScore::toScore).collect(Collectors.toList())));

            return scores;
        }
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard(int offset, int entries) {
        try (Session session = this.sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<LeaderboardEntry> scoreboardQuery = builder
                .createQuery(LeaderboardEntry.class);
            Root<DatabaseScore> scoreRoot = scoreboardQuery.from(DatabaseScore.class);

            // Build query
            scoreboardQuery
                .orderBy(builder.asc(scoreRoot.get("scoreAmount")))
                .select(builder.construct(LeaderboardEntry.class,
                    scoreRoot.get("name"), scoreRoot.get("scoreAmount")));

            // Execute query with given parameters
            List<LeaderboardEntry> result = session
                .createQuery(scoreboardQuery)
                .setFirstResult(offset)
                .setMaxResults(entries)
                .getResultList();

            // Return result
            session.getTransaction().commit();
            return result;
        }
    }
}
