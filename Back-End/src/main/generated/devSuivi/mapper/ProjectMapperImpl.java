package devSuivi.mapper;

import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ProjectDetailDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.ProjectNewDto;
import devSuivi.dto.ServerListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.Contribution;
import devSuivi.entity.Project;
import devSuivi.entity.Server;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-14T12:20:25+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public Project toProject(ProjectNewDto projectNewDto) {
        if ( projectNewDto == null ) {
            return null;
        }

        Project project = new Project();

        project.setName( projectNewDto.name() );
        project.setDescription( projectNewDto.description() );
        project.setQuota( projectNewDto.quota() );
        project.setStatus( projectNewDto.status() );

        return project;
    }

    @Override
    public ProjectListDto toProjectListDto(Project project) {
        if ( project == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        int quota = 0;
        String status = null;
        ServerListDto server = null;

        id = project.getId();
        name = project.getName();
        description = project.getDescription();
        quota = project.getQuota();
        status = project.getStatus();
        server = serverToServerListDto( project.getServer() );

        long totalDuration = project.getContributions().stream().mapToLong(c -> c.getDuration()).sum();

        ProjectListDto projectListDto = new ProjectListDto( id, name, description, quota, status, server, totalDuration );

        return projectListDto;
    }

    @Override
    public ProjectDetailDto toProjectDetailDto(Project project, List<UserListDto> users, List<ContributionListDto> contributions) {
        if ( project == null && users == null && contributions == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        int quota = 0;
        String status = null;
        ServerListDto server = null;
        List<ContributionListDto> contributions1 = null;
        if ( project != null ) {
            id = project.getId();
            name = project.getName();
            description = project.getDescription();
            quota = project.getQuota();
            status = project.getStatus();
            server = serverToServerListDto( project.getServer() );
            contributions1 = contributionListToContributionListDtoList( project.getContributions() );
        }
        List<UserListDto> users1 = null;
        List<UserListDto> list1 = users;
        if ( list1 != null ) {
            users1 = new ArrayList<UserListDto>( list1 );
        }

        ProjectDetailDto projectDetailDto = new ProjectDetailDto( id, name, description, quota, status, server, users1, contributions1 );

        return projectDetailDto;
    }

    @Override
    public void updateProject(ProjectNewDto projectNewDto, Project project) {
        if ( projectNewDto == null ) {
            return;
        }

        project.setName( projectNewDto.name() );
        project.setDescription( projectNewDto.description() );
        project.setQuota( projectNewDto.quota() );
        project.setStatus( projectNewDto.status() );
    }

    protected ServerListDto serverToServerListDto(Server server) {
        if ( server == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;

        id = server.getId();
        name = server.getName();
        description = server.getDescription();

        ServerListDto serverListDto = new ServerListDto( id, name, description );

        return serverListDto;
    }

    protected ContributionListDto contributionToContributionListDto(Contribution contribution) {
        if ( contribution == null ) {
            return null;
        }

        Long id = null;
        long duration = 0L;
        LocalDate dateContribution = null;

        id = contribution.getId();
        duration = contribution.getDuration();
        dateContribution = contribution.getDateContribution();

        ContributionListDto contributionListDto = new ContributionListDto( id, duration, dateContribution );

        return contributionListDto;
    }

    protected List<ContributionListDto> contributionListToContributionListDtoList(List<Contribution> list) {
        if ( list == null ) {
            return null;
        }

        List<ContributionListDto> list1 = new ArrayList<ContributionListDto>( list.size() );
        for ( Contribution contribution : list ) {
            list1.add( contributionToContributionListDto( contribution ) );
        }

        return list1;
    }
}
