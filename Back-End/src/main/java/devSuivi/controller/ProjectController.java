package devSuivi.controller;

import devSuivi.dto.*;
import devSuivi.service.ProjectService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectListDto> getAllProjects(){
        return projectService.getAllProjects();
    }

    @GetMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Project trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Project non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ProjectDetailDto> getProjectById(@PathVariable Long id, Authentication authentication){
        return new ResponseEntity<>(projectService.getProjectById(id, authentication), HttpStatus.OK);
    }

    @GetMapping("/{id:[\\d]+}/users")
    public List<UserListDto> getUsersByProjectId(@PathVariable Long id, Authentication authentication){
        return projectService.getUsersByProjectId(id, authentication);
    }

    @GetMapping("/{id:[\\d]+}/contributions")
    public List<ContributionListDto> getContributionsByProjectId(@PathVariable Long id, Authentication authentication){
        return projectService.getContributionsByProjectId(id, authentication);
    }

    @PostMapping
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "Project Ajouté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDetailDto.class)) )
    } )
    public ResponseEntity<ProjectDetailDto> saveProject(@RequestBody @Valid ProjectNewDto projectNewDto){
        return new ResponseEntity<>(projectService.saveProject(projectNewDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Project trouvé et modifié",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Project non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ProjectDetailDto> updateProject(@PathVariable Long id, @RequestBody @Valid ProjectNewDto projectNewDto){
        return new ResponseEntity<>(projectService.updateProject(id, projectNewDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "Project supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project non trouvé",
                    content = @Content)
    } )
    public void deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
    }
}
