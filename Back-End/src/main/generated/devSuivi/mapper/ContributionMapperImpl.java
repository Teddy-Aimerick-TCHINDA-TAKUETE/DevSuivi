package devSuivi.mapper;

import devSuivi.dto.ContributionDetailDto;
import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ContributionNewDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.Contribution;
import devSuivi.entity.Project;
import devSuivi.entity.User;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-15T22:36:14+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ContributionMapperImpl implements ContributionMapper {

    @Override
    public Contribution toContribution(ContributionNewDto contributionNewDto) {
        if ( contributionNewDto == null ) {
            return null;
        }

        Contribution contribution = new Contribution();

        contribution.setDuration( contributionNewDto.duration() );
        contribution.setDateContribution( contributionNewDto.dateContribution() );

        return contribution;
    }

    @Override
    public ContributionListDto toContributionListDto(Contribution contribution) {
        if ( contribution == null ) {
            return null;
        }

        Long idUser = null;
        Long idProject = null;
        String projectName = null;
        Long id = null;
        long duration = 0L;
        LocalDate dateContribution = null;

        idUser = contributionUserId( contribution );
        idProject = contributionProjectId( contribution );
        projectName = contributionProjectName( contribution );
        id = contribution.getId();
        duration = contribution.getDuration();
        dateContribution = contribution.getDateContribution();

        String userName = contribution.getUser() == null ? null : contribution.getUser().getFirstName() + " " + contribution.getUser().getLastName();

        ContributionListDto contributionListDto = new ContributionListDto( id, duration, dateContribution, idUser, userName, idProject, projectName );

        return contributionListDto;
    }

    @Override
    public ContributionDetailDto toContributionDetailDto(Contribution contribution, UserListDto user, ProjectListDto project) {
        if ( contribution == null && user == null && project == null ) {
            return null;
        }

        Long id = null;
        long duration = 0L;
        LocalDate dateContribution = null;
        if ( contribution != null ) {
            id = contribution.getId();
            duration = contribution.getDuration();
            dateContribution = contribution.getDateContribution();
        }
        UserListDto user1 = null;
        user1 = user;
        ProjectListDto project1 = null;
        project1 = project;

        ContributionDetailDto contributionDetailDto = new ContributionDetailDto( id, duration, dateContribution, user1, project1 );

        return contributionDetailDto;
    }

    @Override
    public void update(ContributionNewDto contributionNewDto, Contribution contribution) {
        if ( contributionNewDto == null ) {
            return;
        }

        contribution.setDuration( contributionNewDto.duration() );
        contribution.setDateContribution( contributionNewDto.dateContribution() );
    }

    private Long contributionUserId(Contribution contribution) {
        User user = contribution.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private Long contributionProjectId(Contribution contribution) {
        Project project = contribution.getProject();
        if ( project == null ) {
            return null;
        }
        return project.getId();
    }

    private String contributionProjectName(Contribution contribution) {
        Project project = contribution.getProject();
        if ( project == null ) {
            return null;
        }
        return project.getName();
    }
}
