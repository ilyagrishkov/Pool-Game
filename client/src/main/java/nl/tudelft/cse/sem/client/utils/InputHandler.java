package nl.tudelft.cse.sem.client.utils;

import static nl.tudelft.cse.sem.client.utils.Constants.SPEED_LIMIT;

import com.badlogic.gdx.InputProcessor;
import nl.tudelft.cse.sem.client.screens.GameScreen;

public class InputHandler implements InputProcessor {

    private static final int SHOT_STRENGTH_THRESHOLD = 1;
    transient GameScreen gameScreen;
    transient PhysicsEngine physicsEngine;

    public InputHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.physicsEngine = gameScreen.getPhysicsEngine();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    //endregion

    //region mouse
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameScreen.getGame().noMove() && !gameScreen.getMoveCueBall()) {
            physicsEngine.getCue().setPosition(gameScreen.getCam(), screenX, screenY);
            physicsEngine.getCue().setDirection(physicsEngine.getCue()
                    .getDirection(physicsEngine.getCueBall()).inverse());
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (gameScreen.getGame().noMove() && !gameScreen.getMoveCueBall()) {
            float strength =
                    2 * physicsEngine.getCue().getStrength(physicsEngine.getCueBall()) < SPEED_LIMIT
                            ? physicsEngine.getCue().getStrength(physicsEngine.getCueBall())
                            : SPEED_LIMIT;

            if (strength > SHOT_STRENGTH_THRESHOLD) {
                gameScreen.getGame().shoot(strength);
                gameScreen.setShowFoul(false);
            }
        }
        if (gameScreen.getMoveCueBall()) {
            gameScreen.setMoveCueBall(false);
            gameScreen.getPhysicsEngine().getDynamicsWorld().addRigidBody(physicsEngine.getCueBall()
                    .getBody());
            gameScreen.renderCue(true);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (gameScreen.getGame().noMove() && !gameScreen.getMoveCueBall()) {
            physicsEngine.getCue().strike(gameScreen.getCam(), screenX, screenY,
                    physicsEngine.getCueBall());
        }

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!gameScreen.getMoveCueBall()) {
            physicsEngine.getCue().rotate(physicsEngine.getCueBall(), gameScreen.getCam(), screenX,
                    screenY);
            physicsEngine.getHelpLine()
                    .rotate(physicsEngine.getCueBall(), gameScreen.getCam(), screenX, screenY);
            if (physicsEngine.getCueBall().getCenter().z < 0) {
                physicsEngine.getCueBall().resetCueBall();
                gameScreen.getGame().setFoul(true);
            }
        }
        if (gameScreen.getMoveCueBall()) {
            gameScreen.getPhysicsEngine().getDynamicsWorld().removeRigidBody(physicsEngine
                    .getCueBall().getBody());
            gameScreen.setCueBallPos(physicsEngine.getCue().getCueBallPosition(gameScreen.getCam(),
                    screenX, screenY));
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
