package capstone.courseweb.jwt.utility;

import capstone.courseweb.jwt.JwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final Aes256Util aes256Util;

    @Value("${jwt.SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${jwt.EXPIRE_TIME}")
    private long EXPIRE_TIME;

    @Value("${jwt.REFRESH_EXPIRE_TIME}")
    private long REFRESH_EXPIRE_TIME;

    @PostConstruct
    void init(){
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public JwtDto createToken(String userId, String userName) {
        String encryptedId = aes256Util.encrypt(userId);
        Claims claims = Jwts.claims().setSubject(encryptedId);
        claims.put("id", userId);
        claims.put("name", userName);

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();


        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();


        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getSubject(Claims claims) { //id 가져오기
        return aes256Util.decrypt(claims.getSubject());
    }

    public Claims getClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token).getBody();


        }catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }catch (Exception e) {
            throw new BadCredentialsException("유효한 토큰이 아닙니다.");
        }

        return claims;
    }
}
