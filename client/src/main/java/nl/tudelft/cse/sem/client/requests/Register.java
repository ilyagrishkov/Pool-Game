package nl.tudelft.cse.sem.client.requests;

import java.io.IOException;
import java.util.Optional;
import nl.tudelft.cse.sem.shared.entity.auth.Registration;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register {

    transient Call<Register> registerCall;

    /**
     * Constructor to make a Login object.
     */
    public Register(String username, String displayname, String password, String token) {
        this(username, displayname, password, token, HttpClient.retrofit);
    }

    /**
     * Constructor to make a Login object.
     */
    public Register(String username, String displayname, String password, String token,
                    Retrofit r) {
        this.registerCall = r
            .create(RegisterRequest.class).registerUser(Registration.builder()
                .username(username)
                .displayName(displayname)
                .multiFactorAuthenticationToken(token)
                .password(password).build());
    }

    /**
     * Method that executes a sync call to the server.
     *
     * @return String containing the message from the server, or an empty object if request fails.
     */
    public Optional<String> makeCall() {
        try {
            Response<Register> response = registerCall.execute();
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
