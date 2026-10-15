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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    public List<ProjectListDto> getProjectsByUserId(Long id, Authentication authentication){
        verifierAcces(authentication, id);
        return getListProject(id);
    }

    @Transactional(readOnly = true)
    public List<ContributionListDto> getContributionsByUserId(Long id, Authentication authentication) {
        verifierAcces(authentication, id);
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

    boolean estResponsable(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + ADMIN) || a.equals("ROLE_" + MANAGER));
    }

    void verifierAcces(Authentication authentication, Long idUser) {
        if (estResponsable(authentication)) {
            return;
        }
        var connecte = loadUserByUsername(authentication.getName());
        if (!connecte.getId().equals(idUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accès refusé : ces données appartiennent à un autre utilisateur.");
        }
    }

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

    List<ProjectListDto> getListProject(Long id){
        return getEntityUserById(id).getAssigns().stream()
                .map(Assign::getProject)
                .map(projectMapper::toProjectListDto)
                .toList();
    }

    private void verifierEmailLibre(User user, String nouvelEmail) {
        if (!user.getEmail().equalsIgnoreCase(nouvelEmail)
                && userRepository.existsByEmailIgnoreCase(nouvelEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé.");
        }
    }

    private void appliquerMotDePasse(User user, String ancienHash, String nouveau) {
        user.setPassword(nouveau == null || nouveau.isBlank()
                ? ancienHash
                : passwordEncoder.encode(nouveau));
    }

    @Transactional
    public UserDetailDto saveUser(UserNewDto userNewDto) {
        if (userNewDto.password() == null || userNewDto.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe est obligatoire.");
        }
        if (userRepository.existsByEmailIgnoreCase(userNewDto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé.");
        }
        verifierRoles(userNewDto.roles());
        var user = userMapper.toUser(userNewDto);
        user.setPassword(passwordEncoder.encode(userNewDto.password()));
        var saved = userRepository.save(user);
        List<ProjectListDto> projects = getListProject(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public UserDetailDto updateUser(Long id, UserNewDto userNewDto){
        var user = getEntityUserById(id);
        verifierEmailLibre(user, userNewDto.email());
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
        var ancienHash = user.getPassword();
        userMapper.updateUser(userNewDto, user);
        appliquerMotDePasse(user, ancienHash, userNewDto.password());
        var saved = userRepository.save(user);
        List<ProjectListDto> projects = getListProject(saved.getId());
        List<ContributionListDto> contributionListDtos = saved.getContributions().stream()
                .map(contributionMapper::toContributionListDto)
                .toList();
        return userMapper.toUserDetailDto(saved, projects, contributionListDtos);
    }

    @Transactional
    public UserDetailDto updateMe(UserNewDto userNewDto, Authentication authentication){
        var user = loadUserByUsername(authentication.getName());
        verifierEmailLibre(user, userNewDto.email());          // remplace le if que je t'avais donné
        var rolesActuels = user.getRoles() == null ? null : new ArrayList<>(user.getRoles());
        var ancienHash = user.getPassword();
        userMapper.updateUser(userNewDto, user);
        user.setRoles(rolesActuels);
        appliquerMotDePasse(user, ancienHash, userNewDto.password());
        var saved = userRepository.save(user);
        List<ProjectListDto> projects = getListProject(saved.getId());
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
