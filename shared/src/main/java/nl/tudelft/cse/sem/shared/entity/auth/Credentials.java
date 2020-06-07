package nl.tudelft.cse.sem.shared.entity.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class Credentials {
    @NonNull
    private String username;

    @NonNull
    private String password;

    private String multiFactorAuthenticationToken;

    public boolean correct() {
        return !username.isBlank() && !password.isBlank();
    }
}
