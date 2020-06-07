package nl.tudelft.cse.sem.server.entity.store;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import lombok.NonNull;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.Score;

/**
 * Interface for the score store. This should handle all things related to scores.
 */
public interface ScoreStore {
    /**
     * Prepares the score store for use. Things like DB connections happen here.
     *
     * @param properties {@link Properties} properties to use
     * @param userStore  {@link UserStore} to use
     */
    void init(@NonNull Properties properties, @NonNull UserStore userStore);

    /**
     * Gives the score store a chance to gracefully shutdown.
     */
    void teardown();

    /**
     * Submits a score for the given user.
     *
     * @param user  The {@link User}
     * @param score An integer which is the score that should be submitted
     * @return An {@link Optional} which contains the submitted score, or empty if the score
     *     could not be submitted.
     */
    Optional<Score> submitScore(@NonNull User user, int score);

    /**
     * Returns a list containing all the registered scores for the current user.
     *
     * @param user The {@link User}
     * @return A {@link List} containing all registered scores for this {@link User}.
     */
    List<Score> getScoresForUser(@NonNull User user);

    /**
     * Returns the current global leaderboard with the given offset and amount of entries.
     *
     * @param offset  An integer representing the offset, so the amount of skipped entries
     * @param entries An integer representing the amount of entries that should be returned
     * @return A {@link List} representing the current global leaderboard,
     *     with the given settings.
     */
    List<LeaderboardEntry> getLeaderboard(int offset, int entries);
}
