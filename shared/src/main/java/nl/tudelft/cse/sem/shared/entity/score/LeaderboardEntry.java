package nl.tudelft.cse.sem.shared.entity.score;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LeaderboardEntry {
    private String displayName;
    private int score;
}
