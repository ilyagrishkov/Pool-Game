package nl.tudelft.cse.sem.server.test.http.endpoint.score;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.http.UnauthorizedResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.http.endpoint.score.RequestScoresEndpoint;
import nl.tudelft.cse.sem.server.test.http.endpoint.EndpointTest;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import nl.tudelft.cse.sem.shared.entity.score.ScoreListResponse;
import org.assertj.core.util.Lists;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequestScoresEndpointTest extends EndpointTest {
    private transient ScoreStore scoreStore;

    /**
     * Sets up dependencies for this endpoint test.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.scoreStore = mock(ScoreStore.class);
        this.endpoint = new RequestScoresEndpoint();

        when(this.database.getScoreStore()).thenReturn(this.scoreStore);
    }

    @Test
    public void shouldReturnNormallyWhenNoScores() {
        when(this.scoreStore.getScoresForUser(this.user)).thenReturn(Lists.emptyList());

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        verifyBody(ScoreListResponse.builder()
            .success(true)
            .scores(new Score[0])
            .build());
    }

    @Test
    public void shouldReturnNormallyWhenScores() {
        Score score = Score.builder().points(42).uuid(UUID.randomUUID()).build();
        List<Score> scores = new ArrayList<>(1);

        scores.add(score);

        when(this.scoreStore.getScoresForUser(this.user)).thenReturn(scores);

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        Score[] result = new Score[scores.size()];
        verifyBody(ScoreListResponse.builder()
            .success(true)
            .scores(scores.toArray(result))
            .build());
    }

    @Test
    public void shouldThrowUnauthorizedWhenNoUser() {
        when(this.jwtManager.getUserFromContext(this.context)).thenReturn(Optional.empty());

        // Make sure the exception was thrown
        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }
}
