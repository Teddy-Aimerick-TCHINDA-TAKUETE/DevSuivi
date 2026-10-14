package devSuivi.mapper;

import devSuivi.dto.AssignIdNewDto;
import devSuivi.dto.AssignListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.AssignId;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-14T12:18:57+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class AssignMapperImpl implements AssignMapper {

    @Override
    public AssignId toAssignId(AssignIdNewDto assignIdNewDto) {
        if ( assignIdNewDto == null ) {
            return null;
        }

        AssignId assignId = new AssignId();

        assignId.setUser( assignIdNewDto.user() );
        assignId.setProject( assignIdNewDto.project() );

        return assignId;
    }

    @Override
    public AssignListDto toAssignListDto(UserListDto user, ProjectListDto project) {
        if ( user == null && project == null ) {
            return null;
        }

        UserListDto user1 = null;
        user1 = user;
        ProjectListDto project1 = null;
        project1 = project;

        AssignListDto assignListDto = new AssignListDto( user1, project1 );

        return assignListDto;
    }
}
