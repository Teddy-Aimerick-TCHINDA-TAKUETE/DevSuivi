package devSuivi.service;

import devSuivi.dto.ContributionDetailDto;
import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ContributionNewDto;
import devSuivi.entity.AssignId;
import devSuivi.entity.Contribution;
import devSuivi.entity.Project;
import devSuivi.entity.User;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.AssignRepositoty;
import devSuivi.repository.ContributionRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private static final String STATUT_EN_COURS = "IN_PROGRESS";

    private final ContributionRepositoty contributionRepositoty;
    private final AssignRepositoty assignRepositoty;
    private final UserService userService;
    private final ProjectService projectService;
    private final ContributionMapper contributionMapper;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ContributionListDto> getAllContributions(){
        return contributionRepositoty.findAll().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContributionDetailDto getContributionById(Long id, Authentication authentication){
        var contribution = getEntityContributionById(id);
        userService.verifierAcces(authentication, contribution.getUser().getId());
        var userListDto = userMapper.toUserListDto(contribution.getUser());
        var projectListDto = projectMapper.toProjectListDto(contribution.getProject());
        return contributionMapper.toContributionDetailDto(contribution, userListDto, projectListDto);
    }

    Contribution getEntityContributionById(Long id){
        if(id == null){
            return null;
        } else {
            return contributionRepositoty.findById(id).orElseThrow( ()-> new NoSuchElementException("Contribution inconnue : " + id) );
        }
    }

    private void verifierContributionAutorisee(User user, Project project) {
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un projet est obligatoire.");
        }
        if (!STATUT_EN_COURS.equals(project.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Le projet « " + project.getName() + " » est " + project.getStatus()
                            + " : on ne peut déclarer une contribution que sur un projet " + STATUT_EN_COURS + ".");
        }
        if (!assignRepositoty.existsById(new AssignId(user.getId(), project.getId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    user.getFirstName() + " " + user.getLastName()
                            + " n'est pas affecté au projet « " + project.getName() + " ».");
        }
    }

    @Transactional
    public ContributionDetailDto saveContribution(ContributionNewDto contributionNewDto, Authentication authentication){
        // Un développeur ne déclare du temps que pour lui-même
        userService.verifierAcces(authentication, contributionNewDto.idUser());
        var contribution = contributionMapper.toContribution(contributionNewDto);
        var user = userService.getEntityUserById(contributionNewDto.idUser());
        userService.verifierDeveloper(user, "déclarer une contribution");
        var project = projectService.getEntityProjectById(contributionNewDto.idProject());
        verifierContributionAutorisee(user, project);
        contribution.setUser(user);
        contribution.setProject(project);
        var saved = contributionRepositoty.save(contribution);
        var userListDto = userMapper.toUserListDto(saved.getUser());
        var projectListDto = projectMapper.toProjectListDto(saved.getProject());
        return contributionMapper.toContributionDetailDto(saved, userListDto, projectListDto);
    }

    @Transactional
    public ContributionDetailDto updateContribution(Long id, ContributionNewDto contributionNewDto, Authentication authentication){
        var contribution = getEntityContributionById(id);
        // La contribution doit être la sienne, et le rester
        userService.verifierAcces(authentication, contribution.getUser().getId());
        userService.verifierAcces(authentication, contributionNewDto.idUser());
        // Qui et sur quel projet, avant que le mapper ne touche à la contribution
        boolean memeUser = contribution.getUser() != null
                && contribution.getUser().getId().equals(contributionNewDto.idUser());
        boolean memeProject = contribution.getProject() != null
                && contribution.getProject().getId().equals(contributionNewDto.idProject());
        contributionMapper.update(contributionNewDto, contribution);
        var user = userService.getEntityUserById(contributionNewDto.idUser());
        userService.verifierDeveloper(user, "déclarer une contribution");
        var project = projectService.getEntityProjectById(contributionNewDto.idProject());
        // Corriger la durée ou la date d'une contribution existante reste possible même
        // si le projet est clôturé depuis ; changer d'utilisateur ou de projet est contrôlé.
        if (!(memeUser && memeProject)) {
            verifierContributionAutorisee(user, project);
        }
        contribution.setUser(user);
        contribution.setProject(project);
        var saved = contributionRepositoty.save(contribution);
        var userListDto = userMapper.toUserListDto(saved.getUser());
        var projectListDto = projectMapper.toProjectListDto(saved.getProject());
        return contributionMapper.toContributionDetailDto(saved, userListDto, projectListDto);
    }

    @Transactional
    public void deleteContribution(Long id, Authentication authentication){
        var contribution = getEntityContributionById(id);
        userService.verifierAcces(authentication, contribution.getUser().getId());
        contributionRepositoty.deleteById(id);
    }
}
