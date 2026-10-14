package devSuivi.service;

import devSuivi.dto.AssignIdNewDto;
import devSuivi.dto.AssignListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.Assign;
import devSuivi.entity.AssignId;
import devSuivi.entity.Project;
import devSuivi.entity.User;
import devSuivi.mapper.AssignMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.AssignRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssignService {

    private final AssignRepositoty assignRepositoty;
    private final UserService userService;
    private final ProjectService projectService;
    private final AssignMapper assignMapper;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<AssignListDto> getAllAssigns(){
        return assignRepositoty.findAll().stream()
                .map((assign) -> {
                    var userListDto = userMapper.toUserListDto(assign.getUser());
                    var projectListDto = projectMapper.toProjectListDto(assign.getProject());
                    return assignMapper.toAssignListDto(userListDto, projectListDto);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public AssignListDto getAssignById(AssignId assignId){
        var assign = getEntityAssingById(assignId);
        var userListDto = userMapper.toUserListDto(assign.getUser());
        var projectListDto = projectMapper.toProjectListDto(assign.getProject());
        return assignMapper.toAssignListDto(userListDto, projectListDto);
    }

    Assign getEntityAssingById(AssignId assignId){
        if(assignId == null){
            return null;
        } else {
            return assignRepositoty.findById(assignId).orElseThrow( ()-> new NoSuchElementException("Assign inconnue : " + assignId) );
        }
    }

    @Transactional
    public AssignListDto saveAssign(AssignIdNewDto assignIdNewDto){
        var assignId =assignMapper.toAssignId(assignIdNewDto);
        User user = userService.getEntityUserById(assignId.getUser());
        userService.verifierDeveloper(user, "être affecté à un projet");
        Project project = projectService.getEntityProjectById(assignId.getProject());
        Assign assign = new Assign(user, project);
        var saved = assignRepositoty.save(assign);
        var userListDto = userMapper.toUserListDto(saved.getUser());
        var projectListDto = projectMapper.toProjectListDto(saved.getProject());
        return assignMapper.toAssignListDto(userListDto, projectListDto);
    }

    @Transactional
    public void deleteAssign(AssignId assignId){
        getEntityAssingById(assignId);
        assignRepositoty.deleteById(assignId);
    }
}
