package devSuivi.mapper;

import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserDetailDto;
import devSuivi.dto.UserListDto;
import devSuivi.dto.UserNewDto;
import devSuivi.entity.Contribution;
import devSuivi.entity.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-09T23:12:13+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserNewDto userNewDto) {
        if ( userNewDto == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( userNewDto.firstName() );
        user.setLastName( userNewDto.lastName() );
        user.setEmail( userNewDto.email() );
        user.setPassword( userNewDto.password() );
        List<String> list = userNewDto.roles();
        if ( list != null ) {
            user.setRoles( new ArrayList<String>( list ) );
        }

        return user;
    }

    @Override
    public UserListDto toUserListDto(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        List<String> roles = null;

        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        List<String> list = user.getRoles();
        if ( list != null ) {
            roles = new ArrayList<String>( list );
        }

        UserListDto userListDto = new UserListDto( id, firstName, lastName, email, roles );

        return userListDto;
    }

    @Override
    public UserDetailDto toUserDetailDto(User user, List<ProjectListDto> projects, List<ContributionListDto> contributions) {
        if ( user == null && projects == null && contributions == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        List<String> roles = null;
        List<ContributionListDto> contributions1 = null;
        if ( user != null ) {
            id = user.getId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            email = user.getEmail();
            List<String> list = user.getRoles();
            if ( list != null ) {
                roles = new ArrayList<String>( list );
            }
            contributions1 = contributionListToContributionListDtoList( user.getContributions() );
        }
        List<ProjectListDto> projects1 = null;
        List<ProjectListDto> list2 = projects;
        if ( list2 != null ) {
            projects1 = new ArrayList<ProjectListDto>( list2 );
        }

        UserDetailDto userDetailDto = new UserDetailDto( id, firstName, lastName, email, roles, projects1, contributions1 );

        return userDetailDto;
    }

    @Override
    public void updateUser(UserNewDto userNewDto, User user) {
        if ( userNewDto == null ) {
            return;
        }

        user.setFirstName( userNewDto.firstName() );
        user.setLastName( userNewDto.lastName() );
        user.setEmail( userNewDto.email() );
        user.setPassword( userNewDto.password() );
        if ( user.getRoles() != null ) {
            List<String> list = userNewDto.roles();
            if ( list != null ) {
                user.getRoles().clear();
                user.getRoles().addAll( list );
            }
            else {
                user.setRoles( null );
            }
        }
        else {
            List<String> list = userNewDto.roles();
            if ( list != null ) {
                user.setRoles( new ArrayList<String>( list ) );
            }
        }
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
