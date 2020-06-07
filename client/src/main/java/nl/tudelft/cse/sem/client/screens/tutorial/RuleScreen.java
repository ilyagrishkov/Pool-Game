package nl.tudelft.cse.sem.client.screens.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.screens.HomeMenuScreen;
import nl.tudelft.cse.sem.client.screens.ScreenWriter;

public class RuleScreen implements Screen {
    private transient StartGame game;

    private transient Stage stage;
    private transient BitmapFont font;
    private transient BitmapFont fontBold;
    private transient BitmapFont fontSmall;

    private transient ScreenWriter writer;

    private transient TextButton rules8;
    private transient TextButton rules9;
    private transient TextButton backButton;

    private transient Skin skinRules8Button;
    private transient Skin skinRules9Button;
    private transient Skin skinBackButton;

    /**
     * Screen containing basic information about the game modes.
     * @param game The game object
     */
    public RuleScreen(StartGame game) {
        this.game = game;

        stage = new Stage();

        //region Fonts
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("monospace.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);

        FreeTypeFontGenerator generatorBold =
                new FreeTypeFontGenerator(Gdx.files.internal("monospacebold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterBold =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterBold.size = 40;
        parameterBold.color = Color.BLACK;
        fontBold = generatorBold.generateFont(parameterBold);
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParamater =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParamater.size = 20;
        smallParamater.color = Color.BLACK;
        fontSmall = generator.generateFont(smallParamater);

        generatorBold.dispose();
        generator.dispose();
        //endregion

        writer = new ScreenWriter.Builder()
                .withTitleFont(fontBold)
                .withHeaderFont(font)
                .withTextFont(fontSmall)
                .withDrawWidth(100)
                .withDrawHeight(100)
                .withDrawSpacing(20)
                .build();

        //region Buttons

        ///////8
        skinRules8Button = new Skin(Gdx.files.internal("Test.json"));

        rules8 = new TextButton("8-ball rules", skinRules8Button);
        rules8.setPosition(200,300);
        rules8.setSize(200,50);
        ///////

        ///////9
        skinRules9Button = new Skin(Gdx.files.internal("Test.json"));

        rules9 = new TextButton("9-ball rules", skinRules9Button);
        rules9.setPosition(800,300);
        rules9.setSize(200,50);
        ///////

        ///////Back
        skinBackButton = new Skin(Gdx.files.internal("Test.json"));

        backButton = new TextButton("Back", skinBackButton);
        backButton.setPosition(20,20);
        backButton.setSize(100,50);
        ///////

        //endregion

        //region Listeners

        rules8.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new RulesEightBallScreen(game));
            }
        });

        rules9.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new RulesNineBallScreen(game));
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new HomeMenuScreen(game));
            }
        });

        //endregion

        stage.addActor(rules8);
        stage.addActor(rules9);
        stage.addActor(backButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Texture background = new Texture("myFloor.jpg");
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);
        stage.getBatch().end();

        writer.begin();
        writer.setDrawWidth(200);
        writer.setDrawHeight(650);
        writer.writeTitle("8-ball");
        writer.setDrawWidth(50);
        writer.setDrawHeight(590);
        String[] text8 = {
            "The goal is to pot the black 8-ball.",
            "This can only be done after potting",
            "  all your other balls.",
            "As there are many balls to pot",
            "  these games tend to take a bit longer.",
            "There are however lots of balls you can hit",
            "  making it a good game to start with."
        };
        writer.writeText(text8);

        writer.setDrawHeight(650);
        writer.setDrawWidth(800);
        writer.writeTitle("9-ball");
        writer.setDrawWidth(650);
        writer.setDrawHeight(590);
        String[] text9 = {
            "The goal is to pot the 9-ball.",
            "This is allowed at any point, ",
            "  as long as you hit the lowest ball first.",
            "As the final ball can be potted at any point",
            "  these games are often shorter.",
            "However they require better aim as you should",
            "  always hit the lowest ball first."
        };
        writer.writeText(text9);
        writer.end();

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
        skinRules8Button.dispose();
        skinRules9Button.dispose();
        skinBackButton.dispose();

        writer.dispose();
        font.dispose();
        fontBold.dispose();
        fontSmall.dispose();
        stage.dispose();
    }
}
