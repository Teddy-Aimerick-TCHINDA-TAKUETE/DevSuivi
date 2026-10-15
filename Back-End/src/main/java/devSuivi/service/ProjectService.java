package devSuivi.service;

import devSuivi.dto.*;
import devSuivi.entity.Assign;
import devSuivi.entity.AssignId;
import devSuivi.entity.Project;
import devSuivi.entity.User;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.AssignRepositoty;
import devSuivi.repository.ProjectRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepositoty projectRepositoty;
    private final AssignRepositoty assignRepositoty;
    @Lazy
    private final ServerService serverService;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;
    private final ContributionMapper contributionMapper;

    @Transactional(readOnly = true)
    public List<ProjectListDto> getAllProjects(){
        return projectRepositoty.findAll().stream()
                .map(projectMapper::toProjectListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailDto getProjectById(Long id, Authentication authentication){
        var project = getEntityProjectById(id);
        var connecte = verifierAccesProjet(project, authentication);
        List<UserListDto> users = getListUser(project.getId());
        return projectMapper.toProjectDetailDto(project, users, contributionsVisibles(project, connecte));
    }

    @Transactional(readOnly = true)
    public List<UserListDto> getUsersByProjectId(Long id, Authentication authentication) {
        verifierAccesProjet(getEntityProjectById(id), authentication);
        return getListUser(id);
    }

    @Transactional(readOnly = true)
    public List<ContributionListDto> getContributionsByProjectId(Long id, Authentication authentication) {
        var project = getEntityProjectById(id);
        return contributionsVisibles(project, verifierAccesProjet(project, authentication));
    }

    Project getEntityProjectById(Long id){
        if(id == null){
            return null;
        } else {
            return projectRepositoty.findById(id).orElseThrow( ()-> new NoSuchElementException("Project inconnue : " + id) );
        }
    }

    List<UserListDto> getListUser(Long id){
        return getEntityProjectById(id).getAssigns().stream()
                .map(Assign::getUser)
                .map(userMapper::toUserListDto)
                .toList();
    }

    List<Project> getAllProjectByServerId(Long id){
        var server = serverService.getEntityServerById(id);
        return projectRepositoty.findAllByServer(server);
    }

    private User verifierAccesProjet(Project project, Authentication authentication) {
        if (userService.estResponsable(authentication)) {
            return null;
        }
        var connecte = userService.loadUserByUsername(authentication.getName());
        if (!assignRepositoty.existsById(new AssignId(connecte.getId(), project.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès refusé : vous n'êtes pas affecté à ce projet.");
        }
        return connecte;
    }

    private List<ContributionListDto> contributionsVisibles(Project project, User connecte) {
        return project.getContributions().stream()
                .filter(c -> connecte == null
                        || (c.getUser() != null && connecte.getId().equals(c.getUser().getId())))
                .map(contributionMapper::toContributionListDto)
                .toList();
    }

    @Transactional
    public ProjectDetailDto saveProject(ProjectNewDto projectNewDto){
        var project = projectMapper.toProject(projectNewDto);
        var server = serverService.getEntityServerById(projectNewDto.idServer());
        project.setServer(server);
        var saved = projectRepositoty.save(project);
        List<UserListDto> users = getListUser(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return projectMapper.toProjectDetailDto(saved, users, contributionListDtos);
    }

    @Transactional
    public ProjectDetailDto updateProject(Long id, ProjectNewDto projectNewDto){
        var project = getEntityProjectById(id);
        projectMapper.updateProject(projectNewDto, project);
        var server = serverService.getEntityServerById(projectNewDto.idServer());
        project.setServer(server);
        var saved = projectRepositoty.save(project);
        List<UserListDto> users = getListUser(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return projectMapper.toProjectDetailDto(saved, users, contributionListDtos);
    }

    @Transactional
    public void deleteProject(Long id){
        getEntityProjectById(id);
        projectRepositoty.deleteById(id);
    }

    void deleteAll(List<Project> projects){
        projectRepositoty.deleteAll(projects);
    }
}
