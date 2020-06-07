package nl.tudelft.cse.sem.server.http.endpoint.score;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import nl.tudelft.cse.sem.shared.entity.score.ScoreListResponse;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles requests for personal score lists.
 */
@Service(path = Endpoints.SCORES)
public class RequestScoresEndpoint implements Endpoint {
    @Override
    public void handle(@NonNull Server server, @NonNull Context context) {
        Optional<User> userFromContext = server.getJwtManager().getUserFromContext(context);

        // If the user could not be retrieved, then there is no auth
        if (userFromContext.isEmpty()) {
            throw new UnauthorizedResponse();
        }

        // Retrieve the user and scores
        User user = userFromContext.get();
        List<Score> scores = server.getDatabase().getScoreStore().getScoresForUser(user);
        Score[] result = new Score[scores.size()];

        // Set response
        context.status(HttpStatus.OK_200);
        context.json(ScoreListResponse.builder()
            .success(true)
            .scores(scores.toArray(result))
            .build());
    }
}
