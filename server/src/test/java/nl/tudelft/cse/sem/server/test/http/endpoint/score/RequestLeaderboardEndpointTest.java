package nl.tudelft.cse.sem.server.test.http.endpoint.score;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.http.BadRequestResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.http.endpoint.score.RequestLeaderboardEndpoint;
import nl.tudelft.cse.sem.server.test.http.endpoint.EndpointTest;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardRequest;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardResponse;
import org.assertj.core.util.Lists;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequestLeaderboardEndpointTest extends EndpointTest {
    private transient ScoreStore scoreStore;

    /**
     * Sets up dependencies for this endpoint test.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.scoreStore = mock(ScoreStore.class);
        this.endpoint = new RequestLeaderboardEndpoint();

        when(this.database.getScoreStore()).thenReturn(this.scoreStore);
    }

    @Test
    public void shouldReturnNormallyWhenNoScores() throws IOException {
        when(this.scoreStore.getLeaderboard(0, 1)).thenReturn(Lists.emptyList());
        setRequestBody(LeaderboardRequest.builder().offset(0).entries(1).build());

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        verifyBody(LeaderboardResponse.builder()
            .success(true)
            .entries(new LeaderboardEntry[0])
            .build());
    }

    @Test
    public void shouldReturnNormallyWhenScores() throws IOException {
        LeaderboardEntry entry = LeaderboardEntry.builder().displayName("wad").score(42).build();
        List<LeaderboardEntry> entries = new ArrayList<>(1);

        entries.add(entry);

        when(this.scoreStore.getLeaderboard(0, 1)).thenReturn(entries);
        setRequestBody(LeaderboardRequest.builder().offset(0).entries(1).build());

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        LeaderboardEntry[] result = new LeaderboardEntry[entries.size()];
        verifyBody(LeaderboardResponse.builder()
            .success(true)
            .entries(entries.toArray(result))
            .build());
    }

    @Test
    public void shouldThrowBadRequestWhenBadOffset() throws IOException {
        setRequestBody(LeaderboardRequest.builder().offset(-1).entries(1).build());

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenBadEntries() throws IOException {
        setRequestBody(LeaderboardRequest.builder().offset(1).entries(-1).build());

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenBadSettings() throws IOException {
        setRequestBody(LeaderboardRequest.builder().offset(-1).entries(-1).build());

        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }
}
