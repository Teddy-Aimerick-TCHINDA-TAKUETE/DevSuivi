package devSuivi.security;

import devSuivi.dto.UserDetailDto;
import devSuivi.dto.UserNewDto;
import devSuivi.security.dto.AuthRequestDto;
import devSuivi.security.dto.AuthResponseDto;
import devSuivi.security.dto.RefreshTokenDto;
import devSuivi.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "User Connecté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class)) )
    } )
    AuthResponseDto login(@RequestBody AuthRequestDto authRequestDto){
        return authService.login(authRequestDto);
    }

    @PostMapping("/register")
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "User enregistré",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class)) )
    } )
    public ResponseEntity<UserDetailDto> register(@RequestBody @Valid UserNewDto userNewDto) {
        var dto = new UserNewDto(userNewDto.firstName(), userNewDto.lastName(),
                userNewDto.email(), userNewDto.password(), List.of("DEVELOPER"));
        return new ResponseEntity<>(userService.saveUser(dto), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @ApiResponses( {
            @ApiResponse(responseCode = "201", description = "User Connecté",
                    content =  @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class)) )
    } )
    AuthResponseDto refresh(@RequestBody @Valid RefreshTokenDto refreshTokenDto){
        return authService.refresh(refreshTokenDto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses( {
            @ApiResponse(responseCode = "204", description = "Refresh token révoqué")
    } )
    void logout(@RequestBody @Valid RefreshTokenDto refreshTokenDto){
        authService.logout(refreshTokenDto);
    }

}
