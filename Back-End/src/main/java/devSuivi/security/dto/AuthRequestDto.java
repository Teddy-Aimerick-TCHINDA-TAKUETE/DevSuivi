package devSuivi.security.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(

        @NotBlank(message = "L'email d'utilisateur est requis")
        String email,

        @NotBlank(message = "Le mot de passe est requis")
        String password
) {}