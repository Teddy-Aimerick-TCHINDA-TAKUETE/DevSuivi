package devSuivi.controller;

import devSuivi.dto.ProjectListDto;
import devSuivi.dto.ServerListDto;
import devSuivi.dto.ServerNewDto;
import devSuivi.service.ServerService;
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
@RequestMapping("/server")
@Tag(name = "server")
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    public List<ServerListDto> getAllServers(){
        return serverService.getAllServers();
    }

    @GetMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Server trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ServerListDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Server non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ServerListDto> getServerById(@PathVariable Long id){
        return new ResponseEntity<>(serverService.getServerById(id), HttpStatus.OK);
    }

    @GetMapping("/{id:[\\d]+}/projects")
    public List<ProjectListDto> getProjectsByServerId(@PathVariable Long id){
        return serverService.getProjectsByServerId(id);
    }

    @PostMapping
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "Server Ajouté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ServerListDto.class)) )
    } )
    public ResponseEntity<ServerListDto> saveServer(@RequestBody @Valid ServerNewDto serverNewDto){
        return new ResponseEntity<>(serverService.saveServer(serverNewDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Server trouvé et modifié",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ServerListDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Server non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ServerListDto> updateServer(@PathVariable Long id, @RequestBody @Valid ServerNewDto serverNewDto){
        return new ResponseEntity<>(serverService.updateServer(id, serverNewDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "Server supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Server non trouvé",
                    content = @Content)
    } )
    public void deleteServer(@PathVariable Long id){
        serverService.deleteServer(id);
    }
}
