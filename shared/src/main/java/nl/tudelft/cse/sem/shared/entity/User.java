package nl.tudelft.cse.sem.shared.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
public class User {
    @Nullable
    private String loginName;
    @Nullable
    private String displayName;
    @NonNull
    private UUID uuid;
    private String multiFactorAuthenticationToken;
}
