package nl.tudelft.cse.sem.shared.entity.score;

import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
//@Builder dont use lombok here, for the sake of the assignment
public class Score {
    @NonNull
    private final UUID uuid;

    private final int points;

    /**
     * Returns a builder of this class.
     *
     * @return A {@link ScoreBuilder}
     */
    public static ScoreBuilder builder() {
        return new ScoreBuilder();
    }

    @SuppressWarnings("PMD") // PMD doesn't want matching field and function names
    public static class ScoreBuilder {
        private UUID uuid;
        private int points;

        /**
         * Set the uuid.
         *
         * @param uuid A uuid
         * @return This score builder
         */
        public ScoreBuilder uuid(UUID uuid) {
            if (uuid == null) {
                throw new NullPointerException();
            }

            this.uuid = uuid;
            return this;
        }

        /**
         * Set the points.
         *
         * @param points The amount of points
         * @return This score builder
         */
        public ScoreBuilder points(int points) {
            this.points = points;
            return this;
        }

        /**
         * Build the final object.
         *
         * @return A {@link Score} instance
         */
        public Score build() {
            return new Score(uuid, points);
        }
    }
}
