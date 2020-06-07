package nl.tudelft.cse.sem.shared.test.entity.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.junit.jupiter.api.Test;

public class ScoreTest {
    @Test
    public void shouldBuildOk() {
        UUID uuid = UUID.randomUUID();
        int score = 42;
        Score buildScore = Score.builder().uuid(uuid).points(score).build();

        assertEquals(uuid, buildScore.getUuid());
        assertEquals(score, buildScore.getPoints());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenInputNull() {
        assertThrows(NullPointerException.class,
            () -> Score.builder().uuid(null).points(42).build());
    }
}
