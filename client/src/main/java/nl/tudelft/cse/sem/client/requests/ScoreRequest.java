package nl.tudelft.cse.sem.client.requests;

import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ScoreRequest {
    @POST(Endpoints.SUBMIT)
    Call<Score> submitScore(@Header("Authorization") String token, @Body LeaderboardEntry score);
}
