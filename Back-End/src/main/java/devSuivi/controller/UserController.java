package devSuivi.controller;

import devSuivi.dto.*;
import devSuivi.repository.UserRepositoty;
import devSuivi.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "user")
public class UserController {

    private final UserService userService;
    private final UserRepositoty userRepositoty;

    @GetMapping
    public List<UserListDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "User trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "User non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<UserDetailDto> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/me")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "User trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserListDto.class)) ),
            @ApiResponse(responseCode = "404", description = "User non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<UserListDto> getUserProfil(Authentication authentication){
        return new ResponseEntity<>(userService.getUserProfil(authentication.getName()), HttpStatus.OK);
    }

    @GetMapping("/{id:[\\d]+}/projects")
    public List<ProjectListDto> getProjectsByUserId(@PathVariable Long id, Authentication authentication){
        return userService.getProjectsByUserId(id, authentication);
    }

    @GetMapping("/{id:[\\d]+}/contributions")
    public List<ContributionListDto> getContributionsByUserId(@PathVariable Long id, Authentication authentication){
        return userService.getContributionsByUserId(id, authentication);
    }

    @PostMapping
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "User Ajouté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class)) )
    } )
    public ResponseEntity<UserDetailDto> saveUser(@RequestBody @Valid UserNewDto userNewDto) {
        return new ResponseEntity<>(userService.saveUser(userNewDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "User trouvé et modifié",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "User non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<UserDetailDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserNewDto userNewDto){
        return new ResponseEntity<>(userService.updateUser(id, userNewDto), HttpStatus.OK);
    }

    @PutMapping("/me")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "User trouvé et modifié",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "User non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<UserDetailDto> updateMe(@RequestBody @Valid UserNewDto userNewDto, Authentication authentication){
        return new ResponseEntity<>(userService.updateMe(userNewDto, authentication), HttpStatus.OK);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "User supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User non trouvé",
                    content = @Content)
    } )
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
