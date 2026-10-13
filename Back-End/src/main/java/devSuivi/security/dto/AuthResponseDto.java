package devSuivi.security.dto;

import java.time.Instant;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        Instant accessExpireAt,
        Instant refreshExpireAt
) {}