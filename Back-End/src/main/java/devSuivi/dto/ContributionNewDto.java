package devSuivi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record ContributionNewDto(
        @NotNull(message = "La contribution doit avoir une duration")
        long duration,
        @NotNull(message = "La contribution doit avoir une date")
        @PastOrPresent(message = "La date de contribution doit etre dans le present ou le passer")
        LocalDate dateContribution,
        @NotNull(message = "La contribution doit etre faite par un user")
        Long idUser,
        @NotNull(message = "La contribution doit etre correspondre a un project")
        Long idProject) {
}
