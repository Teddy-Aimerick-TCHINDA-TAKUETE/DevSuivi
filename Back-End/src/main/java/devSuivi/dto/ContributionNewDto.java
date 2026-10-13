package devSuivi.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContributionNewDto(
        @NotNull(message = "La contribution doit avoir une duration")
        long duration,
        @NotNull(message = "La contribution doit avoir une date")
        LocalDate dateContribution,
        @NotNull(message = "La contribution doit etre faite par un user")
        Long idUser,
        @NotNull(message = "La contribution doit etre correspondre a un project")
        Long idProject) {
}
