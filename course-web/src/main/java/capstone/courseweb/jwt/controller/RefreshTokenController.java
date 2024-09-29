package capstone.courseweb.jwt.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.utility.JwtIssuer;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequiredArgsConstructor
public class RefreshTokenController {
    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;

    @PostMapping("/access-token-recreate")
    public ResponseEntity<JwtDto> refreshToken(@RequestBody JwtDto jwtDto) {
        if (!jwtAuthProvider.validateToken(jwtDto)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 리프레시 토큰 만료 확인
        Claims refreshClaims = jwtIssuer.getClaims(jwtDto.getRefreshToken());
        Date refreshExpirationDate = refreshClaims.getExpiration();
        if (refreshExpirationDate.before(new Date())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }



        Claims accessClaims = jwtIssuer.getClaims(jwtDto.getAccessToken());
        String newAccessToken = String.valueOf(jwtIssuer.createToken(
                accessClaims.get("id", String.class),
                accessClaims.get("name", String.class)
        ));

        JwtDto newTokens = JwtDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(jwtDto.getRefreshToken())
                .build();


        return ResponseEntity.ok(newTokens);
    }
}
