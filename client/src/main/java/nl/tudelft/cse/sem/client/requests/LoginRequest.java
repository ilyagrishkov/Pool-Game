package nl.tudelft.cse.sem.client.requests;

import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.auth.Credentials;
import nl.tudelft.cse.sem.shared.entity.auth.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginRequest {
    @POST(Endpoints.LOGIN)
    Call<TokenResponse> loginUser(@Body Credentials credentials);
}
