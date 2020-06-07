package nl.tudelft.cse.sem.client.requests;

import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface LeaderboardRequest {
    @POST(Endpoints.LEADERBOARD)
    Call<LeaderboardResponse> getLeaderBoard(
            @Body nl.tudelft.cse.sem.shared.entity.score.LeaderboardRequest leaderboardReq);
}