package devSuivi.dto;

import java.util.List;

public record ProjectDetailDto(
        Long id,
        String name,
        String description,
        int quota,
        String status,
        ServerListDto server,
        List<UserListDto> users,
        List<ContributionListDto> contributions) {
}
