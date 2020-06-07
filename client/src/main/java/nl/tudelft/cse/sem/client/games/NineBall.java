package nl.tudelft.cse.sem.client.games;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.screens.GameScreen;

@Data
public class NineBall extends Game {
    private static final int FINAL_BALL = 9;

    private transient boolean player1Won;
    private transient boolean player2Won;

    private transient int player1Potted;
    private transient int player2Potted;

    private transient List<Ball> remainingBalls;


    /**
     * Constructs a game given the screen and the objects.
     *
     */
    public NineBall(Builder builder) {
        super(builder);
        player1Won = false;
        player2Won = false;

        player1Potted = 0;
        player2Potted = 0;

        remainingBalls = new ArrayList<>();
        remainingBalls.addAll(builder.balls);
    }

    @Override
    public void pocketBall(Ball ball) {
        pottedBalls.add(ball);
        potted = true;
        if (ball.getNumber() == FINAL_BALL) {
            if (!player1) {
                player1Won = true;
            } else {
                player2Won = true;
            }
        }
        if (!player1) {
            player1Potted++;
        } else {
            player2Potted++;
        }
    }

    @Override
    protected void handleTurn() {
        if (touchedBalls.size() == 0 || touchedBalls.get(0) != getLowestBall()) {
            foul = true;
        }

        if (potted && !foul) {
            player1 = !player1;//reverse automatic turn switch
        }
        remainingBalls.removeAll(pottedBalls);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private int getLowestBall() {
        int min = 9;
        for (Ball ball:remainingBalls) {
            if (ball.getNumber() != 0 && ball.getNumber() < min) {
                min = ball.getNumber();
            }
        }
        return min;
    }

    @Override
    protected void checkForVictory() {
        if ((player1Won || player2Won) && !foul) {
            screen.goToScoreScreen(calculateScore(player1Won));
        }
        if (player1Won && foul) {
            screen.goToScoreScreen(calculateScore(false));
        }
        if (player2Won && foul) {
            screen.goToScoreScreen(calculateScore(true));
        }
    }

    @Override
    protected void displayType(GameScreen screen) {
        screen.displayLowestBall(getLowestBall());
    }

    /**
     * Lower is better.
     * @param player1Won if player 1 has won
     * @return the achieved score by the winning player
     */
    private int calculateScore(boolean player1Won) {
        if (player1Won) {
            return (player2Potted + 1) * (player1Potted + player2Potted) * player1turnCounter;
        }
        return (player1Potted + 1) * (player1Potted + player2Potted) * player2turnCounter;
    }
}
