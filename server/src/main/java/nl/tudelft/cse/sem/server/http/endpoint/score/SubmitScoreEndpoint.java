package nl.tudelft.cse.sem.server.http.endpoint.score;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import java.util.Optional;
import lombok.NonNull;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.http.endpoint.Service;
import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

/**
 * Handles submitting of scores.
 */
@Service(method = HandlerType.POST, path = Endpoints.SUBMIT)
public class SubmitScoreEndpoint implements Endpoint {
    private static final Logger logger = LogManager.getLogger(SubmitScoreEndpoint.class);

    @Override
    @SuppressWarnings("PMD") //replace with more specific error (DU)
    public void handle(@NonNull Server server, @NonNull Context context) {
        Optional<User> userFromContext = server.getJwtManager().getUserFromContext(context);

        // If the user could not be retrieved, then there is no auth
        if (userFromContext.isEmpty()) {
            throw new UnauthorizedResponse();
        }

        // Retrieve user and the new entry
        User user = userFromContext.get();
        LeaderboardEntry submittedScore = context.bodyAsClass(LeaderboardEntry.class);

        // Make sure the display name is valid
        if (submittedScore.getDisplayName().trim().length() <= 0
            || submittedScore.getDisplayName().length() > 32) {
            throw new BadRequestResponse();
        }

        // Update display name to go with the score
        Optional<User> updatedUser = server.getDatabase().getUserStore().updateUser(user.getUuid(),
            User.builder()
                .uuid(user.getUuid())
                .displayName(submittedScore.getDisplayName())
                .loginName(user.getLoginName())
                .build());

        // Make sure the user was updated, should always succeed
        if (updatedUser.isEmpty()) {
            throw new InternalServerErrorResponse();
        }

        // Save the new entry
        logger.info("Saving score " + submittedScore + " for user '" + user.getLoginName() + "'");
        Optional<Score> savedScore = server.getDatabase().getScoreStore()
            .submitScore(user, submittedScore.getScore());

        // If no score was saved return an internal server error (should not happen)
        if (savedScore.isEmpty()) {
            throw new InternalServerErrorResponse();
        }

        // Set response
        logger.info("Score " + savedScore.get() + " saved for user '" + user.getLoginName()
            + "'!");

        context.status(HttpStatus.OK_200);
        context.json(GenericResponse.builder().success(true).build());
    }
}
