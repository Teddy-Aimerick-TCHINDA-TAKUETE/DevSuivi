package devSuivi.dto;

import java.time.LocalDate;
import java.util.Date;

public record ContributionListDto(
        Long id,
        long duration,
        LocalDate dateContribution) {
}
