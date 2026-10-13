package devSuivi.dto;

import jakarta.validation.constraints.NotNull;

public record AssignIdNewDto(
        @NotNull(message = "Il faut un utilisateur pour l'Assign")
        Long  user,
        @NotNull(message = "Il faut un project pour l'Assign")
        Long  project) {
}
