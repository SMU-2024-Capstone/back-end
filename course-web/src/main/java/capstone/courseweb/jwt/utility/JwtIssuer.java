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

    @Value("${jwt.KEY_ROLES}")
    private String KEY_ROLES;

    @PostConstruct
    void init(){
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public JwtDto createToken(String userId, String userName) { //소셜id, 소셜 name으로
        String encryptedId = aes256Util.encrypt(userId);
        Claims claims = Jwts.claims().setSubject(encryptedId); //claim: jwt에 포함될 정보
        claims.put("id", userId);
        claims.put("name", userName);
        //claims.put("nickname", nickname);
        //claims.put(KEY_ROLES, role);

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

        log.info(accessToken);
        log.info(refreshToken);

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getSubject(Claims claims) { //id 가져오기
        return aes256Util.decrypt(claims.getSubject());
    }

    public Claims getClaims(String token) {
        log.info("getclaim 실행, token 출력: {}", token); /**여기까진 됨**/
        Claims claims;
        try {
            log.info("겟클레임 트라이 실행되는지 확인");
            claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    //.build
                    .parseClaimsJws(token).getBody();

            log.info("claim 복호화 확인: {}", claims.get("id"));

        }catch (ExpiredJwtException e) {
            log.info("겟클레임에서 ExpiredJwtException 오류");
            claims = e.getClaims();
        }catch (Exception e) {
            //System.out.println("getclaim " + claims);
            throw new BadCredentialsException("유효한 토큰이 아닙니다.");
        }

        log.info("겟클레임에서 리턴되는 클레임 출력: {}" , claims);
        log.info("겟클레임에서 리턴되는 클레임의 아이디 출력: {}" , claims.get("id"));
        log.info("겟클레임에서 리턴되는 클레임 출력: {}" , claims.toString());
        return claims;
    }
}
