package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.screens.LoginScreen.BAHN_LABEL;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.SMALL_WINDOW_POS;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.TRANSPARENT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.requests.Leaderboard;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class LeaderboardScreen implements Screen {

    private transient StartGame game;

    private transient Stage stage;

    private transient Texture texture;

    /**
     * Constructor for the SettingsScreen.
     *
     * @param game - the game object
     */
    @SuppressWarnings("PMD")
    public LeaderboardScreen(StartGame game) {
        Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
        window.setPosition(SMALL_WINDOW_POS, window.getPositionY());

        texture = new Texture("FinalLeaderboardScreen.png");

        this.game = game;

        String[] names = new String[5];
        String[] scores = new String[5];

        Optional<String> res = new Leaderboard(0,5).makeCall();
        String result = res.orElse("");
        if (result.length() < 3) {
            for (int i = 0;i < 5; i++) {
                names[i] = "---";
                scores[i] = "---";
            }
        } else {
            result = result.substring(1, result.length() - 1);

            List<String> results = new ArrayList<>(Arrays.asList(result.split("\\), \\(")));
            int i;

            for (i = 0; i < results.size(); i++) {
                List<String> words = Arrays.asList(results.get(i).split(","));
                names[i] = words.get(0).split("=")[1];
                scores[i] = words.get(1).split("=")[1];
            }
            while (i < 5) {
                names[i] = "---";
                scores[i] = "---";
                i++;
            }
        }

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        //skinLabel = new Skin(Gdx.files.internal("TextFieldTest.json"));
        ///////player1
        Label player1 = new Label(names[0], BAHN_LABEL);
        player1.setPosition(160,550);
        player1.setSize(100,50);

        Label player1Score = new Label(scores[0], BAHN_LABEL);
        player1Score.setPosition(360,550);
        player1Score.setSize(100,50);

        ///////player2
        Label player2 = new Label(names[1], BAHN_LABEL);
        player2.setPosition(160,500);
        player2.setSize(100,50);

        Label player2Score = new Label(scores[1], BAHN_LABEL);
        player2Score.setPosition(360,500);
        player2Score.setSize(100,50);

        ///////player3
        Label player3 = new Label(names[2], BAHN_LABEL);
        player3.setPosition(160,450);
        player3.setSize(100,50);

        Label player3Score = new Label(scores[2], BAHN_LABEL);
        player3Score.setPosition(360,450);
        player3Score.setSize(100,50);

        ///////player4
        Label player4 = new Label(names[3], BAHN_LABEL);
        player4.setPosition(160,400);
        player4.setSize(100,50);

        Label player4Score = new Label(scores[3], BAHN_LABEL);
        player4Score.setPosition(360,400);
        player4Score.setSize(100,50);

        ///////player5
        Label player5 = new Label(names[4], BAHN_LABEL);
        player5.setPosition(160,350);
        player5.setSize(100,50);

        Label player5Score = new Label(scores[4], BAHN_LABEL);
        player5Score.setPosition(360,350);
        player5Score.setSize(100,50);

        // Back button
        TextButton backButton = new TextButton("", TRANSPARENT);
        backButton.setPosition(160,160);
        backButton.setSize(260,70);


        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
                dispose();
                game.setScreen(new HomeMenuScreen(game));
            }
        });

        stage.addActor(player1);
        stage.addActor(player1Score);
        stage.addActor(player2);
        stage.addActor(player2Score);
        stage.addActor(player3);
        stage.addActor(player3Score);
        stage.addActor(player4);
        stage.addActor(player4Score);
        stage.addActor(player5);
        stage.addActor(player5Score);
        stage.addActor(backButton);
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
        //skinLabel.dispose();
        //skinBackButton.dispose();

        stage.dispose();
    }
}
