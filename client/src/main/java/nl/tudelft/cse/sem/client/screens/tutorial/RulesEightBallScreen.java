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
import nl.tudelft.cse.sem.client.screens.ScreenWriter;

public class RulesEightBallScreen implements Screen {
    private transient StartGame game;

    private transient Stage stage;
    private transient BitmapFont font;
    private transient BitmapFont fontBold;
    private transient BitmapFont fontSmall;

    private transient ScreenWriter writer;

    private transient TextButton backButton;

    private transient Skin skinBackButton;

    /**
     * Screen containing rules for the 8-ball game mode.
     * @param game The game object
     */
    public RulesEightBallScreen(StartGame game) {
        this.game = game;

        stage = new Stage();

        //region Fonts
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("monospace.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.BLACK;
        font = generator.generateFont(parameter);

        FreeTypeFontGenerator generatorBold =
                new FreeTypeFontGenerator(Gdx.files.internal("monospacebold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterBold =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterBold.size = 35;
        parameterBold.color = Color.BLACK;
        fontBold = generatorBold.generateFont(parameterBold);
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParamater =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParamater.size = 20;
        smallParamater.color = Color.BLACK;
        fontSmall = generator.generateFont(smallParamater);

        generator.dispose();
        generatorBold.dispose();
        //endregion

        int drawWidth = 50;
        int drawHeight = 650;
        int drawSpacing = 20;
        writer = new ScreenWriter.Builder()
                .withTitleFont(fontBold)
                .withHeaderFont(font)
                .withTextFont(fontSmall)
                .withDrawWidth(drawWidth)
                .withDrawHeight(drawHeight)
                .withDrawSpacing(drawSpacing)
                .build();

        ///////Back
        skinBackButton = new Skin(Gdx.files.internal("Test.json"));

        backButton = new TextButton("Back", skinBackButton);
        backButton.setPosition(20,20);
        backButton.setSize(100,50);
        ///////

        backButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new RuleScreen(game));
            }
        });

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

        int drawWidth = 50;
        int drawHeight = 650;
        writer.setDrawHeight(drawHeight);
        writer.setDrawWidth(drawWidth);

        writer.begin();
        writer.writeTitle("8-ball rules");
        writer.writeHeader("Progression of the game:");
        String[] gameProgressionText = {
            "Each player is assigned a ball type (Solids/Stripes) based on the first ball potted.",
            "If you pot one of your balls and have not committed a foul, you shoot again.",
            "After you have potted al your assigned balls, you need to pot the black ball to win.",
            "If you pot the black ball before this you will immediately lose the game.",
            "This is also the case if you pot the black ball while making a foul."
        };
        writer.writeText(gameProgressionText);
        writer.writeHeader("Fouls:");
        String[] foulText = {
            "If a foul is committed the other player places the cue ball and starts their turn.",
            "A foul is committed if...",
            "- You pot the cue ball.",
            "- The first ball you hit is not of your assigned type.",
            "- The first ball you hit is the black ball "
                    + "and you still have balls of your own type remaining."
        };
        writer.writeText(foulText);
        writer.writeHeader("Point system:");
        String[] scoringText = {
            "At the end of a game the victorious player can submit their score to the leaderboard.",
            "This score is calculated according to the following formula:",
            "       Score = Number of balls potted by opponent * total amount of turns",
            "This means a lower score is considered better."
        };
        writer.writeText(scoringText);
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
        skinBackButton.dispose();

        writer.dispose();
        font.dispose();
        fontBold.dispose();
        fontSmall.dispose();
        stage.dispose();
    }
}
