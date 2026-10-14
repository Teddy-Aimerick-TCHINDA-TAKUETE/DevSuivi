package devSuivi.service;

import devSuivi.dto.ContributionDetailDto;
import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ContributionNewDto;
import devSuivi.entity.Contribution;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.ContributionRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepositoty contributionRepositoty;
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
    public ContributionDetailDto getContributionById(Long id){
        var contribution = getEntityContributionById(id);
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

    @Transactional
    public ContributionDetailDto saveContribution(ContributionNewDto contributionNewDto){
        var contribution = contributionMapper.toContribution(contributionNewDto);
        var user = userService.getEntityUserById(contributionNewDto.idUser());
        userService.verifierDeveloper(user, "déclarer une contribution");
        var project = projectService.getEntityProjectById(contributionNewDto.idProject());
        contribution.setUser(user);
        contribution.setProject(project);
        var saved = contributionRepositoty.save(contribution);
        var userListDto = userMapper.toUserListDto(saved.getUser());
        var projectListDto = projectMapper.toProjectListDto(saved.getProject());
        return contributionMapper.toContributionDetailDto(saved, userListDto, projectListDto);
    }

    @Transactional
    public ContributionDetailDto updateContribution(Long id, ContributionNewDto contributionNewDto){
        var contribution = getEntityContributionById(id);
        contributionMapper.update(contributionNewDto, contribution);
        var user = userService.getEntityUserById(contributionNewDto.idUser());
        userService.verifierDeveloper(user, "déclarer une contribution");
        var project = projectService.getEntityProjectById(contributionNewDto.idProject());
        contribution.setUser(user);
        contribution.setProject(project);
        var saved = contributionRepositoty.save(contribution);
        var userListDto = userMapper.toUserListDto(saved.getUser());
        var projectListDto = projectMapper.toProjectListDto(saved.getProject());
        return contributionMapper.toContributionDetailDto(saved, userListDto, projectListDto);
    }

    @Transactional
    public void deleteContribution(Long id){
        getEntityContributionById(id);
        contributionRepositoty.deleteById(id);
    }
}
