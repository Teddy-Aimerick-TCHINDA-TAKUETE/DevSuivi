package devSuivi.mapper;

import devSuivi.dto.*;
import devSuivi.entity.Contribution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContributionMapper {

    Contribution toContribution(ContributionNewDto contributionNewDto);

    ContributionListDto toContributionListDto(Contribution contribution);

    @Mapping(source = "contribution.id", target = "id")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "project", target = "project")
    ContributionDetailDto toContributionDetailDto(Contribution contribution, UserListDto user, ProjectListDto project);

    void update(ContributionNewDto contributionNewDto, @MappingTarget Contribution contribution);
}
