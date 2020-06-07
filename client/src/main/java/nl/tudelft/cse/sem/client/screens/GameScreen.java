package nl.tudelft.cse.sem.client.screens;

import static nl.tudelft.cse.sem.client.utils.Constants.BALL_RADIUS;
import static nl.tudelft.cse.sem.client.utils.Constants.CUE_LENGTH;
import static nl.tudelft.cse.sem.client.utils.Constants.EIGHT_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.NINE_BALL_GAME;
import static nl.tudelft.cse.sem.client.utils.Constants.SCREEN_HEIGHT;
import static nl.tudelft.cse.sem.client.utils.Constants.SMALL_SCREEN_WIDTH;
import static nl.tudelft.cse.sem.client.utils.Constants.SOLIDS_TYPE;
import static nl.tudelft.cse.sem.client.utils.Constants.STRIPES_TYPE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.StartGame;
import nl.tudelft.cse.sem.client.games.EightBall;
import nl.tudelft.cse.sem.client.games.Game;
import nl.tudelft.cse.sem.client.games.NineBall;
import nl.tudelft.cse.sem.client.utils.CollisionHandler;
import nl.tudelft.cse.sem.client.utils.InputHandler;
import nl.tudelft.cse.sem.client.utils.PhysicsEngine;

@Getter
@Setter
public class GameScreen implements Screen {

    //region fieldDeclarations
    private static final int SHOT_STRENGTH_THRESHOLD = 1;

    private transient PerspectiveCamera cam;
    private transient ModelBatch modelBatch;
    private transient SpriteBatch spriteBatch;
    private transient Environment environment;

    private transient StartGame startGame;
    private transient Game game;

    private transient boolean showFoul = false;

    private transient ModelInstance player1m;
    private transient ModelInstance player2m;
    private transient ModelInstance foulm;
    private transient Texture background;

    private transient PhysicsEngine physicsEngine;

    private transient boolean moveCueBall = false;
    private transient Vector3 cueBallPos = new Vector3();
    //endregion

    /**
     * Constructor for a GameScreen.
     *
     * @param startGame - StartGame instance
     */
    public GameScreen(StartGame startGame, int gameMode) {
        this.startGame = startGame;
        Bullet.init();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        background = new Texture("myFloor.jpg");
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 11, 350f);
        cam.lookAt(0, 11f, 0);
        cam.near = 1f;
        cam.far = 700f;
        cam.update();

        physicsEngine = new PhysicsEngine(new Vector3(0, 0, -100f), gameMode);
        new CollisionHandler(this);
        Gdx.input.setInputProcessor(new InputHandler(this));

        Game.Builder gameBuilder = new Game.Builder()
                .withGameScreen(this)
                .withTable(physicsEngine.getTable())
                .withCue(physicsEngine.getCue())
                .withCueBall(physicsEngine.getCueBall())
                .withBalls(physicsEngine.getTable().getBalls());

        if (gameMode == EIGHT_BALL_GAME) {
            this.game = new EightBall(gameBuilder);
        } else if (gameMode == NINE_BALL_GAME) {
            this.game = new NineBall(gameBuilder);
        }
        createTurnModels();
    }

    @Override
    public void render(float delta) {
        physicsEngine.getDynamicsWorld().stepSimulation(delta, 5, 1f / 60f);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        game.processGame();

        if (moveCueBall) {
            physicsEngine.getCueBall().transform.setTranslation(cueBallPos);
            physicsEngine.getCueBall().getBody().proceedToTransform(physicsEngine
                    .getCueBall().transform);
        }

        modelBatch.begin(cam);
        modelBatch.render(physicsEngine.getRenderables(), environment);
        modelBatch.end();
    }

    //region EmptyOverrides
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
    public void show() {

    }

    @Override
    public void hide() {

    }
    //endregion

    @Override
    public void dispose() {
        physicsEngine.dispose();
        modelBatch.dispose();
    }

    /**
     * Goes to the score screen.
     * @param score the score to display
     */
    public void goToScoreScreen(int score) {
        Gdx.graphics.setWindowedMode(SMALL_SCREEN_WIDTH, SCREEN_HEIGHT);
        startGame.setScreen(new ScoreScreen(startGame, score));
    }

    /**
     * Resets cue and help line.
     */
    public void resetCueAndHelpLine() {
        physicsEngine.getCue().transform.setToRotation(Vector3.Z, 90);
        physicsEngine.getCue().transform.setTranslation(physicsEngine.getCueBall().getBody()
                        .getCenterOfMassPosition().x - CUE_LENGTH / 2 - BALL_RADIUS,
                physicsEngine.getCueBall().getBody().getCenterOfMassPosition().y, 3);

        physicsEngine.getHelpLine().transform.setToRotation(Vector3.Z, -90);
        physicsEngine.getHelpLine().transform
                .setTranslation(physicsEngine.getCueBall().getBody().getCenterOfMassPosition().x
                + physicsEngine.getHelpLine().getLength() / 2 + BALL_RADIUS,
                        physicsEngine.getCueBall().getBody()
                .getCenterOfMassPosition().y, 3);
    }

    /**
     * Renders the cue in the environment.
     * @param setCue if the cue should be set
     */
    public void renderCue(boolean setCue) {
        if (setCue) {
            resetCueAndHelpLine();
        }
        modelBatch.render(physicsEngine.getCue(), environment);
        modelBatch.render(physicsEngine.getHelpLine(), environment);
    }

    /**
     * Displays the messages indicating player turn and fouls.
     * @param player1 if it is player 1 turn
     * @param foul if a foul has been made
     */
    public void displayMessages(boolean player1, boolean foul) {
        if (foul) {
            showFoul = true;
        }

        if (player1) {
            modelBatch.render(player1m);

            if (showFoul) {
                modelBatch.render(foulm);
            }
        }

        if (!player1) {
            modelBatch.render(player2m);

            if (showFoul) {
                modelBatch.render(foulm);
            }
        }
    }


    /**
     * Displays the messages indicating which player is which type.
     * @param player1type ball type of player 1
     * @param player2type ball type of player 2
     */
    public void displayType(int player1type, int player2type) {
        if (player1type == SOLIDS_TYPE && player2type == STRIPES_TYPE) {
            Model player1TypeModel = createModel("solidsoak.png");
            ModelInstance player1mType = new ModelInstance(player1TypeModel);
            player1mType.transform.setTranslation(-89.6f, 66, 100);

            Model player2TypeModel = createModel("stripesoak.png");
            ModelInstance player2mType = new ModelInstance(player2TypeModel);
            player2mType.transform.setTranslation(89.6f, 66, 100);

            modelBatch.render(player1mType);
            modelBatch.render(player2mType);
        } else if (player1type == STRIPES_TYPE && player2type == SOLIDS_TYPE) {
            Model player1TypeModel = createModel("stripesoak.png");
            ModelInstance player1mType = new ModelInstance(player1TypeModel);
            player1mType.transform.setTranslation(-89.6f, 66, 100);

            Model player2TypeModel = createModel("solidsoak.png");
            ModelInstance player2mType = new ModelInstance(player2TypeModel);
            player2mType.transform.setTranslation(89.6f, 66, 100);

            modelBatch.render(player1mType);
            modelBatch.render(player2mType);
        }
    }

    /**
     * Displays the lowest ball that has to get hit at the start of the turn.
     * @param lowestBall the lowest ball on the table
     */
    public void displayLowestBall(int lowestBall) {
        String path = lowestBall + "oakdisplay.png";
        Model lowestBallModel = createModel(path);
        ModelInstance lowestBallm = new ModelInstance(lowestBallModel);
        lowestBallm.transform.setTranslation(-89.6f, 66,100);

        modelBatch.render(lowestBallm);
    }



    private Model createModel(String path) {
        ModelBuilder modelBuilder = new ModelBuilder();

        Texture texture = new Texture(path);
        final Material planeMaterial = new Material(TextureAttribute.createDiffuse(texture));

        return modelBuilder.createBox(44.8f, 22.4f, 0.4f, planeMaterial,
                VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);
    }

    private void createTurnModels() {
        Model player1Model = createModel("player1oak.png");
        player1m = new ModelInstance(player1Model);

        player1m.transform.setTranslation(-44.8f, 66, 100);

        Model player2Model = createModel("player2oak.png");
        player2m = new ModelInstance(player2Model);

        player2m.transform.setTranslation(44.8f, 66, 100);

        Model foulModel = createModel("fouloak.png");
        foulm = new ModelInstance(foulModel);

        foulm.transform.setTranslation(0, 66, 100);
    }

    public void setMoveCueBall(boolean moveCueBall) {
        this.moveCueBall = moveCueBall;
    }

    public boolean getMoveCueBall() {
        return this.moveCueBall;
    }
}
