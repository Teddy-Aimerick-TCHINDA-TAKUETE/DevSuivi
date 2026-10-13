package devSuivi.dto;

import jakarta.validation.constraints.NotNull;

public record ProjectNewDto(
        @NotNull(message = "Le project doit avoir un nom")
        String name,
        String description,
        int quota,
        String status,
        @NotNull(message = "Le project doit etre stocker dans un server")
        Long idServer) {
}
