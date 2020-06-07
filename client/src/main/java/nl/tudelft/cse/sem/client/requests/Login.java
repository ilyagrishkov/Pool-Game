package nl.tudelft.cse.sem.client.requests;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.cse.sem.shared.entity.auth.Credentials;
import nl.tudelft.cse.sem.shared.entity.auth.TokenResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login {
    transient Call<TokenResponse> loginCall;

    /**
     * Constructor to make a Login object.
     */
    public Login(String username, String password, String mfaToken) {
        this(username, password, mfaToken, HttpClient.retrofit);
    }

    /**
     * Constructor to make a Login object.
     */
    public Login(String username, String password, String mfaToken, Retrofit r) {
        this.loginCall = r
            .create(LoginRequest.class).loginUser(Credentials.builder()
                .username(username)
                .password(password)
                .multiFactorAuthenticationToken(mfaToken).build());
    }

    /**
     * Method that executes a sync call to the server.
     *
     * @return String containing the message from the server, or an empty object if request fails.
     */
    public Optional<String> makeCall() {
        try {
            Response<TokenResponse> response = loginCall.execute();
            if (response.isSuccessful()) {
                return Optional.of(response.body().toString());
            } else {
                if (response.errorBody() != null && response.errorBody().string().contains("mfa")) {
                    return Optional.of("mfa");
                }
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
