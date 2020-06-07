package nl.tudelft.cse.sem.shared.entity;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class GenericResponse {
    @NonNull
    private final Boolean success;

    @Nullable
    private final String errorMessage;
}
