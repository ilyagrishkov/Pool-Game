package nl.tudelft.cse.sem.client;

import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SMALL_SCREEN_WIDTH;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.util.Optional;
import nl.tudelft.cse.sem.client.requests.HttpClient;

public class Launcher {

    /**
     * Starting point of the program.
     *
     * @param arg - arguments
     */
    public static void main(String[] arg) {
        //TODO: Temporary
        String location = Optional.ofNullable(System.getenv("SERVER_LOCATION"))
            .orElse("https://sem.timanema.net/");
        HttpClient.getClient(location);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowedMode(SMALL_SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setResizable(false);

        new Lwjgl3Application(new StartGame(), config);
    }
}
