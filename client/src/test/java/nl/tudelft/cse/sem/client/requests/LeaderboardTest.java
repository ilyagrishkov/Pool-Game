package nl.tudelft.cse.sem.client.requests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class LeaderboardTest {
    transient MockWebServer server;
    transient Retrofit retrofit;
    transient String username = "laura";

    @BeforeEach
    void setUp() {
        server = new MockWebServer();
        // Start the server.
        try {
            server.start();
        } catch (IOException e) {
            fail();
        }

        // Ask the server for its URL. We need this to make HTTP requests.
        HttpUrl baseUrl = server.url("");
        System.out.println(baseUrl);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void leaderBoardSendSuccessful() {
        server.enqueue(new MockResponse()
                .setResponseCode(200).setBody("{\"message\" : \"OK!\"}"));
        Optional<String> res = new Leaderboard(0, 5, retrofit).makeCall();
        System.out.println(res);
        assertTrue(res.isPresent());
    }


    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void invalidOffset() {
        server.enqueue(new MockResponse().setResponseCode(401));
        Optional r2 = new Leaderboard(-1, 4, retrofit).makeCall();
        assertTrue(r2.isEmpty());
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void invalidEntries() {
        server.enqueue(new MockResponse().setResponseCode(401));
        Optional r2 = new Leaderboard(1, -4, retrofit).makeCall();
        assertTrue(r2.isEmpty());
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void ioexception() {
        Optional<String> r2 = new Leaderboard(0, 10, new Retrofit.Builder()
                .baseUrl(server.url("/wrongpath/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build())
                .makeCall();
        assertTrue(r2.isEmpty());
    }

}