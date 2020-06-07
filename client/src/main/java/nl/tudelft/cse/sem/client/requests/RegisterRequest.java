package nl.tudelft.cse.sem.client.requests;

import nl.tudelft.cse.sem.shared.Endpoints;
import nl.tudelft.cse.sem.shared.entity.auth.Registration;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterRequest {
    @POST(Endpoints.REGISTER)
    Call<Register> registerUser(@Body Registration reg);
}
