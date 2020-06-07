package nl.tudelft.cse.sem.client.requests;

import static org.junit.Assert.assertEquals;
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

class LoginTest {
    transient MockWebServer server;
    transient Retrofit retrofit;
    transient String username = "laura";
    transient String password = "badPass";

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
    public void shouldIndicateMultiFactorWhenEnabled() {
        server.enqueue(new MockResponse()
            .setResponseCode(401).setBody("{\"success\" : \"false\", \"errorMessage\": \"mfa\"}"));
        Optional<String> res = new Login(username, password, null, retrofit).makeCall();

        assertTrue(res.isPresent());
        assertEquals("mfa", res.get());
    }

    @Test
    void authSuccessful() {
        server.enqueue(new MockResponse()
            .setResponseCode(200).setBody("{\"message\" : \"laura\"}"));
        Optional<String> res = new Login(username, password, null, retrofit).makeCall();
        System.out.println(res);
        assertTrue(res.isPresent());
    }


    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void userAlreadyExists() {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setResponseCode(401));
        Optional<String> res =
            new Register(username, "laura.pirca", password, null, retrofit)
                .makeCall();
        Optional<String> r2 =
            new Login("ivar", password, null, retrofit).makeCall();
        assertTrue(r2.isEmpty());
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void wrongPassword() {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setResponseCode(401));
        Optional<String> res = new Register(username, "laura.pirca", password, null,
            retrofit).makeCall();
        Optional<String> r2 = new Login(username, "thisiswrong", null,
            retrofit).makeCall();
        assertTrue(r2.isEmpty());
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    void ioexception() {
        Optional<String> r2 = new Login(username, "", null,
            new Retrofit.Builder()
                .baseUrl(server.url("/wrongpath/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build())
            .makeCall();
        assertTrue(r2.isEmpty());
    }
}