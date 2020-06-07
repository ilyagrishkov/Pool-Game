package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN_LABEL;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.SMALL_WINDOW_POS;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.TRANSPARENT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_WIDTH;
import static nl.tudelft.cse.sem.client.utils.Constants.SMALL_SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.Optional;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.requests.Score;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class ScoreScreen implements Screen {

    private transient int score;
    private transient StartGame game;

    private transient Stage stage;

    private transient Texture texture;

    /**
     * Constructor for the SettingsScreen.
     *
     * @param game - the game object
     */

    public ScoreScreen(StartGame game, int score) {
        Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
        window.setPosition(SMALL_WINDOW_POS, window.getPositionY());

        texture = new Texture("FinalScoreScreen.png");

        this.score = score;
        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Score label
        Label scoreField = new Label(Integer.toString(score), BAHN_LABEL);
        scoreField.setPosition(230, 540);
        scoreField.setSize(200, 80);

        // Username field
        TextField usernameField = new TextField("", BAHN);
        usernameField.setPosition(230, 455);
        usernameField.setSize(200, 80);

        // Home button
        TextButton menuButton = new TextButton("", TRANSPARENT);
        menuButton.setPosition(85, 250);
        menuButton.setSize(210, 70);

        //Leaderboard button
        TextButton leaderboardButton = new TextButton("", TRANSPARENT);
        leaderboardButton.setPosition(85, 140);
        leaderboardButton.setSize(350, 70);

        menuButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                String name = usernameField.getText();
                Optional<String> res = new Score(name, score).makeCall();
                Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
                dispose();
                game.setScreen(new HomeMenuScreen(game));
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                String name = usernameField.getText();
                Optional<String> res = new Score(name, score).makeCall();
                Gdx.graphics.setWindowedMode(SMALL_SCREEN_WIDTH, SCREEN_HEIGHT);
                dispose();
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        stage.addActor(scoreField);
        stage.addActor(usernameField);
        stage.addActor(menuButton);
        stage.addActor(leaderboardButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0);
        stage.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
