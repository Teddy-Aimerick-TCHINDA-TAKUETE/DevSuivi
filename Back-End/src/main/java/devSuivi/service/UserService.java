package devSuivi.service;

import devSuivi.dto.*;
import devSuivi.entity.Assign;
import devSuivi.entity.User;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.UserRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepositoty userRepositoty;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final ContributionMapper contributionMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserListDto> getAllUsers(){
        return userRepositoty.findAll().stream()
                .map(userMapper::toUserListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDetailDto getUserById(Long id){
        var user = getEntityUserById(id);
        List<ProjectListDto> projects = getListProject(user.getId());
        List<ContributionListDto> contributionListDtos = user.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(user, projects, contributionListDtos);
    }

    @Transactional(readOnly = true)
    public UserListDto getUserProfil(String username){
        var user = loadUserByUsername(username);
        return userMapper.toUserListDto(user);
    }

    @Transactional(readOnly = true)
    public List<ProjectListDto> getProjectsByUserId(Long id){
        return getListProject(id);
    }

    @Transactional(readOnly = true)
    public List<ContributionListDto> getContributionsByUserId(Long id) {
        return getEntityUserById(id).getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
    }

    User getEntityUserById(Long id){
        if(id == null){
            return null;
        } else {
            return userRepositoty.findById(id).orElseThrow( ()-> new NoSuchElementException("User inconnue : " + id) );
        }
    }

    List<ProjectListDto> getListProject(Long id){
        return getEntityUserById(id).getAssigns().stream()
                .map(Assign::getProject)
                .map(projectMapper::toProjectListDto)
                .toList();
    }

    @Transactional
    public UserDetailDto saveUser(UserNewDto userNewDto){
        var user = userMapper.toUser(userNewDto);
        user.setPassword(passwordEncoder.encode(userNewDto.password()));
        var saved = userRepositoty.save(user);
        List<ProjectListDto> projects = getListProject(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public UserDetailDto updateUser(Long id, UserNewDto userNewDto){
        var user = getEntityUserById(id);
        userMapper.updateUser(userNewDto, user);
        user.setPassword(passwordEncoder.encode(userNewDto.password()));
        var saved = userRepositoty.save(user);
        List<ProjectListDto> projects = getListProject(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public void deleteUser(Long id){
        getEntityUserById(id);
        userRepositoty.deleteById(id);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null){
            return null;
        } else {
            return userRepositoty.findByEmail(username).orElseThrow( ()-> new UsernameNotFoundException("User inconnue : " + username) );
        }
    }
}
