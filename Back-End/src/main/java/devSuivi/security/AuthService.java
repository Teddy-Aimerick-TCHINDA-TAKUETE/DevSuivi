package devSuivi.security;

import devSuivi.security.dto.AuthRequestDto;
import devSuivi.security.dto.AuthResponseDto;
import devSuivi.security.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        // remove() est atomique : un refresh token ne sert qu'UNE fois (rotation).
        // Si quelqu'un rejoue un ancien jeton, il est déjà supprimé -> 401.
        var refreshData = refreshTokens.remove(dto.refreshToken());
        if (refreshData == null) {
            throw nonAuthentifie("Refresh token invalide");
        }
        if (refreshData.expiresAt().isBefore(Instant.now())) {
            throw nonAuthentifie("Refresh token expiré");
        }
        try {
            var user = userDetailsService.loadUserByUsername(refreshData.username());
            return buildAuthResponse(user.getUsername(), user.getAuthorities());
        } catch (UsernameNotFoundException e) {
            // utilisateur supprimé ou email modifié depuis la connexion
            throw nonAuthentifie("Utilisateur introuvable");
        }
    }

    // Déconnexion : le refresh token est révoqué (sans erreur s'il est déjà inconnu)
    public void logout(RefreshTokenDto dto) {
        refreshTokens.remove(dto.refreshToken());
    }

    //-------
    // Méthodes auxiliaires
    //-------

    // 401 propre, avec un message exploitable par le front (ProblemDetail)
    private ResponseStatusException nonAuthentifie(String message) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }

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

        var now = Instant.now();
        // Ménage : on oublie les refresh tokens expirés (sinon la map grossit sans fin)
        refreshTokens.values().removeIf(d -> d.expiresAt().isBefore(now));
        var accessExpireAt = now.plusSeconds(jwtExpiration);
        var accessToken = createAccessToken(username, authorities, accessExpireAt);
        var refreshExpireAt = now.plusSeconds(refreshExpiration);
        var refreshToken = createRefreshToken(username, refreshExpireAt);
        return new AuthResponseDto(accessToken, refreshToken, accessExpireAt, refreshExpireAt);
    }

}
