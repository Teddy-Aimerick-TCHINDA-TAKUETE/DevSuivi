package devSuivi.controller;

import devSuivi.dto.AssignIdNewDto;
import devSuivi.dto.AssignListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.UserListDto;
import devSuivi.entity.AssignId;
import devSuivi.service.AssignService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assign")
@Tag(name = "assign")
public class AssignController {

    private final AssignService assignService;

    @GetMapping
    public List<AssignListDto> getAllAssigns(){
        return assignService.getAllAssigns();
    }

    @GetMapping("/id")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Assign trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = AssignListDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Assign non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<AssignListDto> getAssignById(@RequestBody AssignId assignId){
        return new ResponseEntity<>(assignService.getAssignById(assignId), HttpStatus.OK);
    }

//    @GetMapping("/userProjects/{id:[\\d]+}")
//    public List<ProjectListDto> getAllProjectsByUseId(@PathVariable Long id){
//        return assignService.getAllProjectsByUserId(id);
//    }
//
//    @GetMapping("/projectUsers/{id:[\\d]+}")
//    public List<UserListDto> getAllUsersByProjectId(@PathVariable Long id){
//        return assignService.getAllUsersByProjectId(id);
//    }

    @PostMapping
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "Assign Ajouté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = AssignListDto.class)) )
    } )
    public ResponseEntity<AssignListDto> saveAssign(@RequestBody @Valid AssignIdNewDto assignIdNewDto){
        return new ResponseEntity<>(assignService.saveAssign(assignIdNewDto), HttpStatus.CREATED);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "Assign supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Assign non trouvé",
                    content = @Content)
    } )
    public void deleteAssign(@RequestBody AssignId assignId){
        assignService.deleteAssign(assignId);
    }
}
