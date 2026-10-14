package devSuivi.service;

import devSuivi.dto.*;
import devSuivi.entity.Assign;
import devSuivi.entity.User;
import devSuivi.mapper.ContributionMapper;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.UserMapper;
import devSuivi.repository.UserRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepositoty userRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final ContributionMapper contributionMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN = "ADMIN";
    private static final String MANAGER = "MANAGER";
    private static final String DEVELOPER = "DEVELOPER";
    private static final Set<String> ROLES_CONNUS = Set.of(ADMIN, MANAGER, DEVELOPER);

    @Transactional(readOnly = true)
    public List<UserListDto> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDetailDto getUserById(Long id){
        var user = getEntityUserById(id);
        List<ProjectListDto> projects = getListProjet(user.getId());
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
    public List<ProjectListDto> getProjetsByUserId(Long id){
        return getListProjet(id);
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
            return userRepository.findById(id).orElseThrow( ()-> new NoSuchElementException("User inconnue : " + id) );
        }
    }

    /**
     * Règle sur les rôles : DEVELOPER est exclusif.
     * Combinaisons valides : ADMIN, MANAGER, ADMIN + MANAGER, ou DEVELOPER seul.
     */
    private void verifierRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'utilisateur doit avoir au moins un rôle.");
        }
        for (String role : roles) {
            if (!ROLES_CONNUS.contains(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Rôle inconnu : " + role + " (ADMIN, MANAGER ou DEVELOPER).");
            }
        }
        if (roles.contains(DEVELOPER) && (roles.contains(ADMIN) || roles.contains(MANAGER))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le rôle DEVELOPER ne peut pas être combiné avec ADMIN ou MANAGER.");
        }
    }

    /**
     * Seul un développeur peut être affecté à un projet ou déclarer une contribution.
     * @param action ce que l'utilisateur voulait faire, ex. "être affecté à un projet"
     */
    void verifierDeveloper(User user, String action) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un utilisateur est obligatoire.");
        }
        if (user.getRoles() == null || !user.getRoles().contains(DEVELOPER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    user.getFirstName() + " " + user.getLastName()
                            + " n'est pas développeur : seul un développeur peut " + action + ".");
        }
    }

    List<ProjectListDto> getListProjet(Long id){
        return getEntityUserById(id).getAssigns().stream()
                .map(Assign::getProject)
                .map(projectMapper::toProjectListDto)
                .toList();
    }

    @Transactional
    public UserDetailDto saveUser(UserNewDto userNewDto) {
        if (userRepository.existsByEmailIgnoreCase(userNewDto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé.");
        }
        verifierRoles(userNewDto.roles());
        var user = userMapper.toUser(userNewDto);
        user.setPassword(passwordEncoder.encode(userNewDto.password()));
        var saved = userRepository.save(user);
        List<ProjectListDto> projects = getListProjet(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public UserDetailDto updateUser(Long id, UserNewDto userNewDto){
        var user = getEntityUserById(id);
        // Les rôles ne sont contrôlés que s'ils changent : un compte de test qui cumule
        // déjà les trois rôles (Teddy) reste modifiable tant qu'on n'y touche pas.
        Set<String> rolesActuels = user.getRoles() == null ? Set.of() : new HashSet<>(user.getRoles());
        if (!rolesActuels.equals(new HashSet<>(userNewDto.roles()))) {
            verifierRoles(userNewDto.roles());
            boolean perdLeRoleDeveloper = !userNewDto.roles().contains(DEVELOPER);
            if (perdLeRoleDeveloper && (!user.getAssigns().isEmpty() || !user.getContributions().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cet utilisateur a des affectations ou des contributions : il doit rester DEVELOPER.");
            }
        }
        userMapper.updateUser(userNewDto, user);
        user.setPassword(passwordEncoder.encode(userNewDto.password()));
        var saved = userRepository.save(user);
        List<ProjectListDto> projects = getListProjet(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public void deleteUser(Long id){
        getEntityUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null){
            return null;
        } else {
            return userRepository.findByEmail(username).orElseThrow( ()-> new UsernameNotFoundException("User inconnue : " + username) );
        }
    }
}
