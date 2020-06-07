package nl.tudelft.cse.sem.shared.entity.auth;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class RefreshRequest {
    @NonNull
    private final String username;

    @NonNull
    private final String refreshToken;
}
