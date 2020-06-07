package nl.tudelft.cse.sem.client;

import com.badlogic.gdx.Game;
import nl.tudelft.cse.sem.client.screens.LoginScreen;

public class StartGame extends Game {

    @Override
    public void create() {
        this.setScreen(new LoginScreen(this));
    }
}
