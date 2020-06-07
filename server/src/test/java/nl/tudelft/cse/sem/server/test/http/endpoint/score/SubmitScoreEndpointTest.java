package nl.tudelft.cse.sem.server.test.http.endpoint.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.ScoreStore;
import nl.tudelft.cse.sem.server.http.endpoint.score.SubmitScoreEndpoint;
import nl.tudelft.cse.sem.server.test.http.endpoint.EndpointTest;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class SubmitScoreEndpointTest extends EndpointTest {
    private transient ScoreStore scoreStore;

    /**
     * Sets up dependencies for this endpoint test.
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        this.scoreStore = mock(ScoreStore.class);
        this.endpoint = new SubmitScoreEndpoint();

        when(this.database.getScoreStore()).thenReturn(this.scoreStore);
    }

    @Test
    public void shouldReturnNormallyWhenNormalScore() throws IOException {
        int points = 42;
        String name = "wda";
        Score score = Score.builder().uuid(UUID.randomUUID()).points(points).build();

        setRequestBody(LeaderboardEntry.builder().displayName(name).score(points).build());
        when(this.scoreStore.submitScore(this.user, points)).thenReturn(Optional.of(score));
        when(this.userStore.updateUser(any(), any())).thenReturn(Optional.of(this.user));

        // Make handle call
        this.endpoint.handle(this.server, this.context);

        // Verify name change
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(this.userStore, times(1))
            .updateUser(any(), captor.capture());
        assertEquals(name, captor.getValue().getDisplayName());

        // Verify 200
        verifyStatusCode(HttpStatus.OK_200);

        // Make sure the content is also correct
        verifyBody(GenericResponse.builder().success(true).build());
    }

    @Test
    public void shouldThrowInternalServerErrorWhenNoUpdate() throws IOException {
        int points = 42;
        String name = "wda";

        setRequestBody(LeaderboardEntry.builder().displayName(name).score(points).build());
        when(this.scoreStore.submitScore(this.user, points)).thenReturn(Optional.empty());
        when(this.userStore.updateUser(any(), any())).thenReturn(Optional.empty());

        // Make sure the exception was thrown
        assertThrows(InternalServerErrorResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenEmptyName() throws IOException {
        int points = 42;
        String name = "";

        setRequestBody(LeaderboardEntry.builder().displayName(name).score(points).build());

        // Make sure the exception was thrown
        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowBadRequestWhenLongName() throws IOException {
        int points = 42;
        String name = "ibhqsowdjisqawdawdadawdawdpoqs[kodjipksdfispk[i";

        setRequestBody(LeaderboardEntry.builder().displayName(name).score(points).build());

        // Make sure the exception was thrown
        assertThrows(BadRequestResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowInternalServerErrorWhenNoSave() throws IOException {
        int points = 42;
        String name = "wda";

        setRequestBody(LeaderboardEntry.builder().displayName(name).score(points).build());
        when(this.scoreStore.submitScore(this.user, points)).thenReturn(Optional.empty());
        when(this.userStore.updateUser(any(), any())).thenReturn(Optional.of(this.user));

        // Make sure the exception was thrown
        assertThrows(InternalServerErrorResponse.class, () -> endpoint.handle(server, context));
    }

    @Test
    public void shouldThrowUnauthorizedWhenNoUser() {
        when(this.jwtManager.getUserFromContext(this.context)).thenReturn(Optional.empty());

        // Make sure the exception was thrown
        assertThrows(UnauthorizedResponse.class, () -> endpoint.handle(server, context));
    }
}
