package devSuivi.security;

import devSuivi.security.dto.AuthRequestDto;
import devSuivi.security.dto.AuthResponseDto;
import devSuivi.security.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//----------
// ChatGPT
//----------

@Service
@RequiredArgsConstructor
public class AuthService {

    //-------
    // Champs
    //-------

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserDetailsService userDetailsService;

    record RefreshData(String username, Instant expiresAt) {
    }

    private final Map<String, RefreshData> refreshTokens =
            new ConcurrentHashMap<>();

    @Value("${jwt.expiration:3600}")
    private long jwtExpiration;

    @Value("${jwt.refresh.expiration:864000}")
    private long refreshExpiration;

    //-------
    // Actions
    //-------

    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto dto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        return buildAuthResponse(authentication.getName(), authentication.getAuthorities());
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refresh(RefreshTokenDto dto) {
        var refreshToken = dto.refreshToken();
        var refreshData = refreshTokens.get(refreshToken);
        if (refreshData == null) {
            throw new BadCredentialsException("Refresh refreshData invalide");
        }
        if (refreshData.expiresAt().isBefore(Instant.now())) {
            refreshTokens.remove(refreshToken);
            throw new BadCredentialsException("Refresh refreshData expiré");
        }
        var user = userDetailsService.loadUserByUsername(refreshData.username());
        return buildAuthResponse(user.getUsername(), user.getAuthorities());
    }

    //-------
    // Méthodes auxiliaires
    //-------

    private String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities, Instant expireAt) {
        var roles = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a ->a.startsWith("ROLE_"))
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .toList();
        var claims = JwtClaimsSet.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(expireAt)
                .build();
        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    private String createRefreshToken(String username, Instant expireAt) {
        var refreshToken = UUID.randomUUID().toString();
        var refreshData = new RefreshData(username, expireAt);
        refreshTokens.put(refreshToken, refreshData);
        return refreshToken;
    }

    private AuthResponseDto buildAuthResponse(String username, Collection<? extends GrantedAuthority> authorities) {

        System.out.println("----------------------");
        for( var a : authorities) {
            System.out.println(a);
        }
        System.out.println("----------------------");

        var now = Instant.now();
        var accessExpireAt = now.plusSeconds(jwtExpiration);
        var accessToken = createAccessToken(username, authorities, accessExpireAt);
        var refreshExpireAt = now.plusSeconds(refreshExpiration);
        var refreshToken = createRefreshToken(username, refreshExpireAt);
        return new AuthResponseDto(accessToken, refreshToken, accessExpireAt, refreshExpireAt);
    }

}
