package devSuivi.service;

import devSuivi.dto.*;
import devSuivi.entity.Assign;
import devSuivi.entity.Project;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.ProjectRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepositoty projectRepositoty;
    @Lazy
    private final ServerService serverService;
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
    public ProjectDetailDto getProjectById(Long id){
        var project = getEntityProjectById(id);
        List<UserListDto> users = getListUser(project.getId());
        List<ContributionListDto> contributionListDtos = project.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return projectMapper.toProjectDetailDto(project, users, contributionListDtos);
    }

    @Transactional(readOnly = true)
    public List<UserListDto> getUsersByProjectId(Long id) {
        return getListUser(id);
    }

    @Transactional(readOnly = true)
    public List<ContributionListDto> getContributionsByProjectId(Long id) {
        return getEntityProjectById(id).getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
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
