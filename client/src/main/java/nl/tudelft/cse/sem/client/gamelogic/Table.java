package nl.tudelft.cse.sem.client.gamelogic;

import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import nl.tudelft.cse.sem.client.gamelogic.gameobjects.GameObject;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.Ball;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.balls.BallFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.Cue;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue.CueFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLine;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline.HelpLineFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.plane.PlaneFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.pockets.PocketFactory;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.walls.WallFactory;


@Getter
@Setter
public class Table implements Disposable {

    private transient List<Ball> balls;
    private List<GameObject> walls;
    private List<GameObject> pockets;
    private Cue cue;
    private HelpLine helpLine;
    private GameObject plane;

    private transient List<GameObject> renderables;


    public static class Builder {
        private transient int type;
        private transient BallFactory ballFactory;
        private transient WallFactory wallFactory;
        private transient PlaneFactory planeFactory;
        private transient PocketFactory pocketFactory;
        private transient CueFactory cueFactory;
        private transient HelpLineFactory helpLineFactory;

        /**
         * Game mode.
         *
         * @param type - game mode
         * @return - Builder
         */
        public Builder ofType(int type) {
            this.type = type;

            return this;
        }

        /**
         * Ball Factory.
         *
         * @param ballFactory - ball factory
         * @return - Builder
         */
        public Builder withBallFactory(BallFactory ballFactory) {
            this.ballFactory = ballFactory;

            return this;
        }

        /**
         * Wall Factory.
         *
         * @param wallFactory - wall factory
         * @return - Builder
         */
        public Builder withWallFactory(WallFactory wallFactory) {
            this.wallFactory = wallFactory;

            return this;
        }

        /**
         * Cue Factory.
         *
         * @param cueFactory - cue factory
         * @return - Builder
         */
        public Builder withCueFactory(CueFactory cueFactory) {
            this.cueFactory = cueFactory;

            return this;
        }

        /**
         * Plane factory.
         *
         * @param planeFactory - plane factory
         * @return - Builder
         */
        public Builder withPlaneFactory(PlaneFactory planeFactory) {
            this.planeFactory = planeFactory;

            return this;
        }

        /**
         * Pocket factory.
         *
         * @param pocketFactory - pocket factory
         * @return - Builder
         */
        public Builder withPocketFactory(PocketFactory pocketFactory) {
            this.pocketFactory = pocketFactory;

            return this;
        }

        /**
         * Help line factory.
         *
         * @param helpLineFactory - help line factory
         * @return - Builder
         */
        public Builder withHelpLineFactory(HelpLineFactory helpLineFactory) {
            this.helpLineFactory = helpLineFactory;

            return this;
        }

        /**
         * Build a Table instance.
         *
         * @return - a new table instance
         */
        public Table build() {
            Table table = new Table();
            table.balls = ballFactory.constructBalls(type);

            table.walls = wallFactory.constructAll();

            table.plane = planeFactory.construct();

            table.pockets = pocketFactory.constructAll();

            table.cue = cueFactory.construct();

            table.helpLine = helpLineFactory.construct();

            return table;
        }
    }

    private Table() {

    }

    /**
     * Get cue ball instance.
     *
     * @return - cue ball instance
     */
    public Ball getCueBall() {
        return (Ball) balls.get(0);
    }

    /**
     * Returns a ball instance with a specified number.
     *
     * @param number - bal number
     * @return - Ball instance
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public GameObject getBall(int number) {
        for (GameObject ball : balls) {

            if (((Ball) ball).getNumber() == number) {

                return ball;
            }
        }

        return null;
    }

    /**
     * Get all models that should be rendered.
     *
     * @return - renderable models
     */
    public List<GameObject> getRenderables() {
        this.renderables = new ArrayList<>();

        renderables.addAll(balls);
        renderables.add(plane);

        return renderables;
    }

    @Override
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void dispose() {
        for (GameObject obj : walls) {

            obj.dispose();
        }

        for (GameObject obj : balls) {

            obj.dispose();
        }
    }
}
