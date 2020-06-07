package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.screens.LoginScreen.SMALL_WINDOW_POS;
import static nl.tudelft.cse.sem.client.screens.LoginScreen.TRANSPARENT;
import static nl.tudelft.cse.sem.client.utils.Constants.EIGHT_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.NINE_BALL_GAME;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.screens.tutorial.RuleScreen;

public class HomeMenuScreen implements Screen {

    private transient StartGame game;

    private transient Stage stage;

    private transient Texture texture;

    /**
     * Constructor for the HomeMenuScreen.
     *
     * @param game - the game object
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    //Will go away once we start using actual different textures
    public HomeMenuScreen(StartGame game) {
        Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);

        Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
        window.setPosition(SMALL_WINDOW_POS - 300, window.getPositionY());

        texture = new Texture("FinalHomeScreen.png");

        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Play 8-ball button
        TextButton playButton8 = new TextButton("", TRANSPARENT);
        playButton8.setPosition(170,485);
        playButton8.setSize(260,60);

        // Play 9-ball button
        TextButton playButton9 = new TextButton("", TRANSPARENT);
        playButton9.setPosition(170, 330);
        playButton9.setSize(260, 60);

        // Rules button
        TextButton ruleButton = new TextButton("", TRANSPARENT);
        ruleButton.setPosition(820, 485);
        ruleButton.setSize(180, 60);

        //Leaderboard button
        TextButton leaderboardButton = new TextButton("", TRANSPARENT);
        leaderboardButton.setPosition(820,325);
        leaderboardButton.setSize(290,60);

        // Exit button
        TextButton exitButton = new TextButton("", TRANSPARENT);
        exitButton.setPosition(820,170);
        exitButton.setSize(150,60);
        //

        playButton8.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                //game.setScreen(new GameScreen(game, EIGHT_BALL_GAME));
                game.setScreen(new GameScreen(game, EIGHT_BALL_GAME));
            }
        });

        playButton9.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new GameScreen(game, NINE_BALL_GAME));
            }
        });

        ruleButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new RuleScreen(game));
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                Gdx.graphics.setWindowedMode(SMALL_SCREEN_WIDTH, SCREEN_HEIGHT);
                dispose();
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });



        stage.addActor(playButton8);
        stage.addActor(playButton9);
        stage.addActor(ruleButton);
        stage.addActor(leaderboardButton);
        stage.addActor(exitButton);
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
