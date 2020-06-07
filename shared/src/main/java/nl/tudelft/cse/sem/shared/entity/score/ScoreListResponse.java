package nl.tudelft.cse.sem.shared.entity.score;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;

@SuperBuilder
@Data
public class ScoreListResponse extends GenericResponse {
    @NonNull
    private final Score[] scores;
}
