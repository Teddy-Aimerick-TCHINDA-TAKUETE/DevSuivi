package devSuivi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserNewDto(
        String firstName,
        String lastName,
        @NotNull(message = "L'utilisateur doit avoir un email")
        @Email(message = "Il doit sagoir d'un email")
        String email,
        @NotNull(message = "L'utilisateur doit avoir un mot de passe")
        String password,
        @NotEmpty(message = "L'utilisateur doit avoir au moins un role")
        List<String> roles) {
}
