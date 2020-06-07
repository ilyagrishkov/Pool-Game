package nl.tudelft.cse.sem.client.games;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import nl.tudelft.cse.sem.client.gamelogic.Table;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.screens.GameScreen;

@Data
public abstract class Game {
    transient GameScreen screen;
    transient Table table;
    transient Cue cue;
    transient Ball cueBall;
    transient List<Ball> balls;

    transient boolean setCue = true;
    transient boolean player1 = true;
    transient boolean moveInProgress = false;
    transient boolean foul = false;
    transient boolean potted = false;

    transient List<Integer> touchedBalls = new ArrayList<>();
    transient List<Ball> pottedBalls = new ArrayList<>();

    transient int player1turnCounter = 0;
    transient int player2turnCounter = 0;

    public static class Builder {
        transient GameScreen gameScreen;
        transient Table table;
        transient Cue cue;
        transient Ball cueBall;
        transient List<Ball> balls;

        /**
         * Game screen.
         *
         * @param gameScreen - game screen
         * @return - Builder
         */
        public Builder withGameScreen(GameScreen gameScreen) {
            this.gameScreen = gameScreen;

            return this;
        }

        /**
         * Table.
         *
         * @param table - table
         * @return - Builder
         */
        public Builder withTable(Table table) {
            this.table = table;

            return this;
        }

        /**
         * Cue.
         *
         * @param cue - cue
         * @return - Builder
         */
        public Builder withCue(Cue cue) {
            this.cue = cue;

            return this;
        }

        /**
         * Cue ball.
         *
         * @param cueBall - cue ball
         * @return - Builder
         */
        public Builder withCueBall(Ball cueBall) {
            this.cueBall = cueBall;

            return this;
        }

        /**
         * Balls on the table.
         *
         * @param balls - balls
         * @return - Builder
         */
        public Builder withBalls(List<Ball> balls) {
            this.balls = balls;

            return this;
        }
    }

    /**
     * Constructs a game given the screen and the objects.
     */
    public Game(Builder builder) {
        this.screen = builder.gameScreen;
        this.table = builder.table;
        this.cue = builder.cue;
        this.cueBall = builder.cueBall;
        this.balls = builder.balls;
    }

    /**
     * Adds the ball to the list of touched balls.
     *
     * @param ball the touched ball
     */
    public void addTouchedBall(Ball ball) {
        if (!touchedBalls.contains(ball.getNumber())) {
            touchedBalls.add(ball.getNumber());
        }
    }

    /**
     * Adds ball to potted list, override if more is needed.
     *
     * @param ball the pocketed ball
     */
    public void pocketBall(Ball ball) {
        pottedBalls.add(ball);
        potted = true;
    }

    /**
     * Shoots the cue and handles the start of the turn.
     *
     * @param strength the strength too shoot with
     */
    public void shoot(float strength) {
        cueBall.shoot(cue.getDirection(cueBall).inverse(), strength);
        setCue = false;
        moveInProgress = true;

        if (player1) {
            player1turnCounter++;
        } else {
            player2turnCounter++;
        }
        player1 = !player1;
    }

    /**
     * Handles the game.
     */
    public void processGame() {
        if (noMove()) {
            if (moveInProgress) {
                moveInProgress = false;
                handleTurn();
            }
            if (foul) {
                screen.setMoveCueBall(true);
            }
            screen.displayMessages(player1, foul);
            displayType(screen);
            checkForVictory();
            touchedBalls = new ArrayList<>();
            pottedBalls = new ArrayList<>();
            potted = false;
            if (!screen.getMoveCueBall()) {
                screen.renderCue(setCue);
            }

            foul = false;
            setCue = false;
        }
    }

    /**
     * Handles a turn.
     */
    protected abstract void handleTurn();

    /**
     * Checks if the game is won.
     */
    protected abstract void checkForVictory();

    /**
     * Displays the type of the balls.
     */
    protected abstract void displayType(GameScreen screen);

    /**
     * Indicates if there are balls still moving.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public boolean noMove() {
        for (GameObject ball : table.getBalls()) {
            if (((Ball) ball).isMoving()) {
                setCue = true;
                return false;
            }
        }
        return true;
    }
}
