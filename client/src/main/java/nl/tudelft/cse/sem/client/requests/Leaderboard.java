package nl.tudelft.cse.sem.client.requests;

import java.io.IOException;
import java.util.Optional;

import nl.tudelft.cse.sem.shared.entity.score.LeaderboardResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Leaderboard {
    transient Call<LeaderboardResponse> leaderboardCall;

    /**
     * Constructor to make a Login object.
     */
    public Leaderboard(int offset, int entries) {
        this(offset, entries, HttpClient.retrofit);
    }

    /**
     * Constructor to make a Login object.
     */
    public Leaderboard(int offset, int entries, Retrofit r) {
        this.leaderboardCall = r
                .create(LeaderboardRequest.class)
                .getLeaderBoard(nl.tudelft.cse.sem.shared.entity.score.LeaderboardRequest
                        .builder()
                        .entries(entries)
                        .offset(offset).build());
    }

    /**
     * Method that executes a sync call to the server.
     *
     * @return String containing the message from the server, or an empty object if request fails.
     */
    public Optional<String> makeCall() {
        try {
            Response<LeaderboardResponse> response = leaderboardCall.execute();
            if (response.isSuccessful()) {
                return Optional.of(response.body().toString().replace("LeaderboardEntry", "")
                        .replace("LeaderboardResponse(", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("))", ")")
                        .replace("entries=", ""));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
