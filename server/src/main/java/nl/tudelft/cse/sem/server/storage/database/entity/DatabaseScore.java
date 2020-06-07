package nl.tudelft.cse.sem.server.storage.database.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import nl.tudelft.cse.sem.shared.entity.score.Score;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class DatabaseScore {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @NonNull
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    @ToString.Exclude
    private DatabaseUser user;

    @NonNull
    private int scoreAmount;

    /**
     * Builds a {@link Score} instance based on this score.
     *
     * @return A regular {@link Score} score.
     */
    public Score toScore() {
        return Score.builder().points(this.scoreAmount).uuid(this.uuid).build();
    }
}
