package devSuivi.controller;

import devSuivi.dto.ContributionDetailDto;
import devSuivi.dto.ContributionListDto;
import devSuivi.dto.ContributionNewDto;
import devSuivi.service.ContributionService;
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
@RequestMapping("/contribution")
@Tag(name = "contribution")
public class ContributionController {

    private final ContributionService contributionService;

    @GetMapping
    public List<ContributionListDto> getAllContributions(){
        return contributionService.getAllContributions();
    }

    @GetMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Contribution trouvé",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ContributionDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Contribution non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ContributionDetailDto> getContributionById(@PathVariable Long id, Authentication authentication){
        return new ResponseEntity<>(contributionService.getContributionById(id, authentication), HttpStatus.OK);
    }

    @PostMapping
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "Contribution Ajouté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ContributionDetailDto.class)) )
    } )
    public ResponseEntity<ContributionDetailDto> saveContribution(@RequestBody @Valid ContributionNewDto contributionNewDto, Authentication authentication){
        return new ResponseEntity<>(contributionService.saveContribution(contributionNewDto, authentication), HttpStatus.CREATED);
    }

    @PutMapping("/{id:[\\d]+}")
    @ApiResponses( {
            @ApiResponse(responseCode = "200", description = "Contribution trouvé et modifié",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = ContributionDetailDto.class)) ),
            @ApiResponse(responseCode = "404", description = "Contribution non trouvé",
                    content = @Content)
    } )
    public ResponseEntity<ContributionDetailDto> updateContribution(@PathVariable Long id, @RequestBody @Valid ContributionNewDto contributionNewDto, Authentication authentication){
        return new ResponseEntity<>(contributionService.updateContribution(id, contributionNewDto, authentication), HttpStatus.OK);
    }

    @DeleteMapping("/{id:[\\d]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "Contribution supprimé",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Contribution non trouvé",
                    content = @Content)
    } )
    public void deleteContribution(@PathVariable Long id, Authentication authentication){
        contributionService.deleteContribution(id, authentication);
    }
}
