package nl.tudelft.cse.sem.client.requests;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.cse.sem.client.CurrentLogin;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;



public class Score {
    transient Call<Score> scoreCall;

    /**
     * Constructor to make a Login object.
     */
    public Score(String username, int score) {
        this(username, score, HttpClient.retrofit);
    }

    /**
     * Constructor to make a Login object.
     */
    public Score(String username, int score, Retrofit r) {
        this.scoreCall = r
                .create(ScoreRequest.class).submitScore("Bearer " + CurrentLogin.token,
                        LeaderboardEntry.builder()
                        .displayName(username)
                        .score(score)
                        .build());
    }

    /**
     * Method that executes a sync call to the server.
     *
     * @return String containing the message from the server, or an empty object if request fails.
     */
    public Optional<String> makeCall() {
        try {
            Response<Score> response = scoreCall.execute();
            if (response.isSuccessful()) {
                return Optional.of(response.message());
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
