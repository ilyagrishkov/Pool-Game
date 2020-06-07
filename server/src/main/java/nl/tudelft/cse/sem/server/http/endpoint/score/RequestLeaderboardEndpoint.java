package nl.tudelft.cse.sem.server.http.endpoint.score;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import java.util.List;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardRequest;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardResponse;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles requests for the leaderboard.
 */
@Service(method = HandlerType.POST, path = Endpoints.LEADERBOARD)
public class RequestLeaderboardEndpoint implements Endpoint {
    @Override
    public void handle(@NonNull Server server, @NonNull Context context) {
        LeaderboardRequest request = context.bodyAsClass(LeaderboardRequest.class);

        // Make sure the given parameters are valid
        if (request.getEntries() < 0 || request.getOffset() < 0) {
            throw new BadRequestResponse();
        }

        // Retrieve leaderboard
        List<LeaderboardEntry> entries = server.getDatabase().getScoreStore()
            .getLeaderboard(request.getOffset(), request.getEntries());
        LeaderboardEntry[] result = new LeaderboardEntry[entries.size()];

        // Set response
        context.status(HttpStatus.OK_200);
        context.json(LeaderboardResponse.builder()
            .success(true)
            .entries(entries.toArray(result))
            .build());
    }
}
