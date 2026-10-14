package devSuivi.mapper;

import devSuivi.dto.ContributionDetailDto;
import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ContributionNewDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.Contribution;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-14T12:18:57+0200",
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

        Long id = null;
        long duration = 0L;
        LocalDate dateContribution = null;

        id = contribution.getId();
        duration = contribution.getDuration();
        dateContribution = contribution.getDateContribution();

        ContributionListDto contributionListDto = new ContributionListDto( id, duration, dateContribution );

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
}
