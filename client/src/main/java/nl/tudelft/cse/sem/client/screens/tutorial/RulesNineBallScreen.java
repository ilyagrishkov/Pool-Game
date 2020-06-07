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

public class RulesNineBallScreen implements Screen {
    private transient StartGame game;

    private transient Stage stage;
    private transient BitmapFont font;
    private transient BitmapFont fontBold;
    private transient BitmapFont fontSmall;

    private transient ScreenWriter writer;

    private transient TextButton backButton;

    private transient Skin skinBackButton;

    /**
     * Screen containing rules for the 9-ball gamemode.
     * @param game The game object
     */
    public RulesNineBallScreen(StartGame game) {
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
        writer.writeTitle("9-ball rules");
        writer.writeHeader("Progression of the game:");
        String[] gameProgressionText = {
            "The goal of 9-ball is to pot the 9-ball.",
            "This must be accomplished by first hitting the lowest remaining ball.",
            "If at any point you pot the 9-ball without committing a foul you win.",
            "If you however pot the 9-ball while committing a foul you lose.",
            "If you pot a ball without committing a foul you may go again."
        };
        writer.writeText(gameProgressionText);
        writer.writeHeader("Fouls:");
        String[] foulText = {
            "If a foul is committed the other player places the cue ball and starts their turn.",
            "A foul is committed if...",
            "- You pot the cue ball.",
            "- The first ball you hit is not the lowest ball."
        };
        writer.writeText(foulText);
        writer.writeHeader("Point system:");
        String[] scoringText = {
            "At the end of a game the victorious player can submit their score to the leaderboard.",
            "This score is calculated according to the following formula:",
            "       Score = Number of balls potted by opponent * Total amount of balls potted",
            "           * Turns taken by the victorious player",
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
