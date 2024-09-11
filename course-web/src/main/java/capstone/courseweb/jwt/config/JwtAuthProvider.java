package capstone.courseweb.jwt.config;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthProvider { //토큰 인증 처리

    private final UserDetailsService userDetailsService;
    private final JwtIssuer jwtIssuer;

    private final MemberRepository memberRepository;

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


        /*String role = claims.get("role", String.class);
        if (role == null || !role.equals("admin")) {
            // 사용자가 관리자가 아님
            return false;
        }*/


        /*if (!jwtIssuer.verifySignature(token)) {
            // 토큰의 서명이 올바르지 않음
            return false;
        }*/


        return true;
    }

    public boolean validateToken(JwtDto jwtDto) {
        if (!StringUtils.hasText(jwtDto.getAccessToken())
                || !StringUtils.hasText(jwtDto.getRefreshToken())) {
            // return false;
            return true;
        }

        Claims accessClaims = jwtIssuer.getClaims(jwtDto.getAccessToken());
        Claims refreshClaims = jwtIssuer.getClaims(jwtDto.getRefreshToken());

        return accessClaims != null && refreshClaims != null
                && jwtIssuer.getSubject(accessClaims).equals(jwtIssuer.getSubject(refreshClaims));
    }

    public Authentication getAuthentication(String token) {
        log.info("getAuthentication에서 토큰 잘 받나 확인: {}", token);

        Claims claims = jwtIssuer.getClaims(token);
        String id = claims.get("id").toString();//소셜id나와야됨
        //String id = jwtIssuer.getSubject(claims); //userid
        log.info("token으로 가져온 클레임의 id(소셜 id여야함.): {}", id);
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);

        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }
}
