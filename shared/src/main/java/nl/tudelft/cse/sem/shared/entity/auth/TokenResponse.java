package nl.tudelft.cse.sem.shared.entity.auth;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import nl.tudelft.cse.sem.shared.entity.GenericResponse;

@SuperBuilder
@Data
public class TokenResponse extends GenericResponse {
    @NonNull
    private final String token;

    @NonNull
    private final String refreshToken;
}
