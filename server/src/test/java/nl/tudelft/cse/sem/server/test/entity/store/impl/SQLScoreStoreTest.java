package nl.tudelft.cse.sem.server.test.entity.store.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.testing.NullPointerTester;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import nl.tudelft.cse.sem.server.Bootstrap;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.impl.SQLScoreStore;
import nl.tudelft.cse.sem.server.entity.store.impl.SQLUserStore;
import nl.tudelft.cse.sem.shared.entity.score.LeaderboardEntry;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:abbreviationaswordinname") // Can't do much about SQL being in there
public class SQLScoreStoreTest {
    private transient SQLUserStore userStore;
    private transient SQLScoreStore db;
    private transient String username = "ivar";
    private transient String password = "badPassword";
    private transient User user;

    @BeforeEach
    void before() {
        this.userStore = new SQLUserStore();
        this.db = new SQLScoreStore();
        this.user = User.builder()
            .uuid(UUID.randomUUID())
            .displayName("display_name")
            .loginName(this.username)
            .build();

        Properties properties = Bootstrap.getDefaultDatabaseProperties();

        // Get properties for test db
        properties.put(Environment.URL,
            "jdbc:mysql://" + System.getProperty("mysql.location")
                + System.getProperty("mysql.port")
                + "/" + System.getProperty("mysql.db")
                + "?autoReconnect=true&createDatabaseIfNotExist=true");
        properties.put(Environment.PASS, System.getProperty("mysql.pw"));
        properties.put(Environment.USER, System.getProperty("mysql.user"));
        properties.put(Environment.HBM2DDL_AUTO, "create");

        this.userStore.init(properties);
        this.db.init(properties, this.userStore);
    }

    @AfterEach
    void after() {
        this.db.teardown();
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.setDefault(User.class, this.user);
        tester.testAllPublicStaticMethods(SQLScoreStore.class);
        tester.testAllPublicInstanceMethods(this.db);
    }

    @Test
    void testScore() {
        Optional<User> user = this.userStore.addUser(this.user, this.password, null);

        // Make sure the user was added
        assertTrue(user.isPresent());

        int scoreAmount = 10;
        int scoreAmount2 = 20;
        this.db.submitScore(user.get(), scoreAmount2);
        this.db.submitScore(user.get(), scoreAmount);
        List<Score> scores = this.db.getScoresForUser(user.get());

        assertEquals(scores.get(0).getPoints(), scoreAmount);
    }

    //PMD complains about scoreAmountIncluded being undefined which is wrong.
    @Test
    @SuppressWarnings("PMD")
    void testLeaderboard() {
        Optional<User> user = this.userStore.addUser(this.user, this.password, null);

        // Make sure the user was added
        assertTrue(user.isPresent());

        int scoreAmountIncluded = 10;
        int scoreAmountExcluded = 20;
        for (int i = 0; i < 5; i++) {
            this.db.submitScore(user.get(), scoreAmountIncluded);
        }
        this.db.submitScore(user.get(), scoreAmountExcluded);

        List<LeaderboardEntry> leaderboard = this.db.getLeaderboard(0, 5);

        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry leaderboardEntry = leaderboard.get(i);
            assertEquals(leaderboardEntry.getScore(), scoreAmountIncluded);
        }
    }
}
