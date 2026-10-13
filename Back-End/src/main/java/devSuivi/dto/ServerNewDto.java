package devSuivi.dto;

import jakarta.validation.constraints.NotNull;

public record ServerNewDto(
        @NotNull(message = "Le server doit avoir un nom")
        String name,
        String description) {
}
