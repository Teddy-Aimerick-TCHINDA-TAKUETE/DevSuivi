package devSuivi.mapper;

import devSuivi.dto.*;
import devSuivi.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toProject(ProjectNewDto projectNewDto);

    @Mapping(target = "totalDuration",
            expression = "java(project.getContributions().stream().mapToLong(c -> c.getDuration()).sum())")
    ProjectListDto toProjectListDto(Project project);

    ProjectDetailDto toProjectDetailDto(Project project, List<UserListDto> users, List<ContributionListDto> contributions);

    void updateProject(ProjectNewDto projectNewDto, @MappingTarget Project project);
}
