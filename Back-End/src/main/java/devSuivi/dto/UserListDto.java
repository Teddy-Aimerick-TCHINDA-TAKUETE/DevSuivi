package devSuivi.dto;

import java.util.List;

public record UserListDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        List<String> roles) {
}
