package devSuivi.mapper;

import devSuivi.dto.AssignIdNewDto;
import devSuivi.dto.AssignListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.AssignId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssignMapper {

    AssignId toAssignId(AssignIdNewDto assignIdNewDto);

    AssignListDto toAssignListDto(UserListDto user, ProjectListDto project);
}
