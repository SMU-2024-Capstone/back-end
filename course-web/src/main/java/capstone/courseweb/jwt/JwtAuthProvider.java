package capstone.courseweb.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider { //토큰 인증 처리

    private final UserDetailsService userDetailsService;
    private final JwtIssuer jwtIssuer;

    public boolean validateToken(String token) { //인증
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = jwtIssuer.getClaims(token);
        if (claims == null) {
            return false;
        }

        Date expirationDate = claims.getExpiration();
        if (expirationDate != null && expirationDate.before(new Date())) {
            // 토큰이 만료됨
            return false;
        }

        String role = claims.get("role", String.class);
        if (role == null || !role.equals("admin")) {
            // 사용자가 관리자가 아님
            return false;
        }


        /*if (!jwtIssuer.verifySignature(token)) {
            // 토큰의 서명이 올바르지 않음
            return false;
        }*/


        return true;
    }

    public boolean validateToken(JwtDto jwtDto) {//재발급
        if (!StringUtils.hasText(jwtDto.getAccessToken())
                || !StringUtils.hasText(jwtDto.getRefreshToken())) {
            return false;
        }

        Claims accessClaims = jwtIssuer.getClaims(jwtDto.getAccessToken());
        Claims refreshClaims = jwtIssuer.getClaims(jwtDto.getRefreshToken());

        return accessClaims != null && refreshClaims != null
                && jwtIssuer.getSubject(accessClaims).equals(jwtIssuer.getSubject(refreshClaims));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtIssuer.getClaims(token);
        String id = jwtIssuer.getSubject(claims);
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);

        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }
}
