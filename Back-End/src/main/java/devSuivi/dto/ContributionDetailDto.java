package devSuivi.dto;

import java.time.LocalDate;

public record ContributionDetailDto(
        Long id,
        long duration,
        LocalDate dateContribution,
        UserListDto user,
        ProjectListDto project) {
}
