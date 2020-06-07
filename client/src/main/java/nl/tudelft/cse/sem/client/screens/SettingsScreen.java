package nl.tudelft.cse.sem.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import nl.tudelft.cse.sem.client.StartGame;

public class SettingsScreen implements Screen {

    private transient StartGame game;

    private transient Stage stage;

    private transient TextButton backButton;

    private transient Skin skinBackButton;

    /**
     * Constructor for the SettingsScreen.
     *
     * @param game - the game object
     */

    public SettingsScreen(StartGame game) {
        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        ///////BackButton
        skinBackButton = new Skin(Gdx.files.internal("Test.json"));

        backButton = new TextButton("Go back", skinBackButton);
        backButton.setPosition(100,100);
        backButton.setSize(200,100);

        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new HomeMenuScreen(game));
            }
        });

        stage.addActor(backButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
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
        skinBackButton.dispose();

        stage.dispose();
    }

}
