package nl.tudelft.cse.sem.shared.entity.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class Registration extends Credentials {
    @NonNull
    private String displayName;

    @Override
    public boolean correct() {
        return super.correct() && !displayName.isBlank();
    }
}
