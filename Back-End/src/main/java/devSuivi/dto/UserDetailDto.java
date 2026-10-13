package devSuivi.dto;

import java.util.List;

public record UserDetailDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        List<String> roles,
        List<ProjectListDto> projects,
        List<ContributionListDto> contributions) {
}
