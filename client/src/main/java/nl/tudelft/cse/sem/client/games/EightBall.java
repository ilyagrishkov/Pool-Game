package nl.tudelft.cse.sem.client.games;

import static nl.tudelft.cse.sem.client.utils.Constants.BLACK_BALL;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.screens.GameScreen;

@Data
public class EightBall extends Game {
    private static final int NUMBER_OF_BALLS_OF_ONE_TYPE = 7;

    private transient List<Integer> player1balls = new ArrayList<>();
    private transient List<Integer> player2balls = new ArrayList<>();

    private transient int player1type;
    private transient int player2type;
    private transient boolean firstHit = true;

    private transient boolean player1lastHit = false;
    private transient boolean player2lastHit = false;
    private transient boolean player1lost = false;
    private transient boolean player2lost = false;
    private transient boolean blackPotted = false;


    public EightBall(Builder builder) {
        super(builder);
    }

    @Override
    protected void handleTurn() {
        if (touchedBalls.size() == 0) {
            foul = true;
        } else if ((!player1 && !player1lastHit) || (player1 && !player2lastHit)) {
            if (touchedBalls.get(0) == BLACK_BALL) {
                foul = true;
            }
        }

        boolean firstHitCheck = player1balls.size() == 0 && player2balls.size() == 0;
        distributePottedBalls();
        checkRules(firstHitCheck);
        if (blackPotted && foul) {
            player1lost = !player1; //If turns is switched to 2 then it was 1 that lost
            player2lost = player1;
        }
        blackPotted = false;
    }

    @Override
    protected void checkForVictory() {
        boolean p1Wins = player2lost || (player1balls.size() == NUMBER_OF_BALLS_OF_ONE_TYPE + 1);
        boolean p2Wins = player1lost || (player2balls.size() == NUMBER_OF_BALLS_OF_ONE_TYPE + 1);
        if (p1Wins ^ p2Wins) {
            screen.goToScoreScreen(calculateScore(p1Wins));
        }
        if (p1Wins && p2Wins) {
            screen.goToScoreScreen(calculateScore(!player1));
        }
    }

    @Override
    protected void displayType(GameScreen screen) {
        screen.displayType(player1type, player2type);
    }

    protected int calculateScore(boolean player1won) {
        if (player1won) {
            return (player2balls.size() + 1) * (player1turnCounter + player2turnCounter);
        } else {
            return (player1balls.size() + 1) * (player1turnCounter + player2turnCounter);
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void checkRules(boolean firstHitCheck) {
        if (!firstHit) {
            if (checkFirstTouchedBall(firstHitCheck)) {
                foul = true;
            }

            if (potted && !foul) {
                player1 = !player1;
            }

            if (player1balls.size() == NUMBER_OF_BALLS_OF_ONE_TYPE) {
                player1lastHit = true;
            }

            if (player2balls.size() == NUMBER_OF_BALLS_OF_ONE_TYPE) {
                player2lastHit = true;
            }
        }
    }

    private boolean checkFirstTouchedBall(boolean firstHitCheck) {
        return touchedBalls.size() == 0 || (!firstHitCheck && touchedBalls.get(0) != 8
                && balls.get(touchedBalls.get(0)).getType() != getCurrentType());
    }

    private int getCurrentType() {
        return (!player1 ? player1type : player2type);
    }


    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void distributePottedBalls() {
        for (Ball ball : pottedBalls) {
            if (player1type == ball.getType()) {
                player1balls.add(ball.getNumber());
            } else {
                player2balls.add(ball.getNumber());
            }
        }
    }

    /**
     * Updates all variables related to pocketing the ball.
     *
     * @param ball the pocketed ball
     */
    @Override
    public void pocketBall(Ball ball) {
        if (ball.getType() == BLACK_BALL) {
            blackPotted = true;
            //keep in mind the switch of players happens after shooting a ball.
            if (player1) {
                if (player2lastHit) {
                    player2balls.add(ball.getNumber());
                } else {
                    player2lost = true;
                }
            } else {
                if (player1lastHit) {
                    player1balls.add(ball.getNumber());
                } else {
                    player1lost = true;
                }
            }
            return;
        }
        if (firstHit) {
            assignBallTypeToPlayers(ball);
        }
        savePottedBalls(ball);
    }

    private void assignBallTypeToPlayers(Ball ball) {
        firstHit = false;

        if (!player1) {
            player1type = ball.getType();
            player2type = ball.getOppositeType();

        } else {
            player2type = ball.getType();
            player1type = ball.getOppositeType();
        }
    }

    private void savePottedBalls(Ball ball) {
        if (!player1) {
            if (player1type == ball.getType()) {
                potted = true;
            }

        } else {
            if (player2type == ball.getType()) {
                potted = true;
            }
        }
        pottedBalls.add(ball);
    }
}
