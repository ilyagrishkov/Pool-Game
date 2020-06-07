package nl.tudelft.cse.sem.shared.entity.score;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LeaderboardRequest {
    private final int offset;
    private final int entries;
}
