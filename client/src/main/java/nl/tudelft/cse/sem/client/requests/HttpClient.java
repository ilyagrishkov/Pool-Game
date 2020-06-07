package nl.tudelft.cse.sem.client.requests;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class HttpClient {
    static transient Retrofit retrofit;

    private HttpClient() {
    }

    /**
     * Sets up the Retrofit interface.
     *
     * @param path URL to send the requests to.
     * @return Retrofit Client that makes the requests.
     */
    public static Retrofit getClient(String path) {
        retrofit = new Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
