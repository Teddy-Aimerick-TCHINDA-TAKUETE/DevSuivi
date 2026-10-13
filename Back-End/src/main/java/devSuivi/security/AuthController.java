package devSuivi.security;

import devSuivi.security.dto.AuthRequestDto;
import devSuivi.security.dto.AuthResponseDto;
import devSuivi.security.dto.RefreshTokenDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    AuthResponseDto login(@RequestBody AuthRequestDto authRequestDto){
        return authService.login(authRequestDto);
    }

    @PostMapping("/refresh")
    AuthResponseDto refresh(@RequestBody RefreshTokenDto refreshTokenDto){
        return authService.refresh(refreshTokenDto);
    }

}
