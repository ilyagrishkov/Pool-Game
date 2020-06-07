package nl.tudelft.cse.sem.client.requests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;


class HttpClientTest {

    transient Retrofit retrofit;
    transient MockWebServer server;

    @BeforeEach
    void setUp() {
        server = new MockWebServer();
    }

    @AfterEach
    void tearDown() {
        try {
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getClient() {
        HttpUrl baseUrl = server.url("");
        System.out.println(baseUrl);
        retrofit = HttpClient.getClient(baseUrl.toString());
        assertEquals(retrofit.baseUrl(), baseUrl);
    }
}