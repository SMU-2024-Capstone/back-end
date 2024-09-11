package capstone.courseweb.jwt.filter;


import capstone.courseweb.jwt.config.JwtAuthProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { //jwt 토큰 검증

    private final JwtAuthProvider jwtAuthProvider;
    public static final String JWT_HEADER_KEY = "Authorization";



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveTokenFromRequest(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtAuthProvider.validateToken(token)) {
            Authentication auth = jwtAuthProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth); //사용자 인증정보 설정
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) { //http요청에서 Authorization 헤더에 있는 jwt 토큰 추출.
        String token = request.getHeader(JWT_HEADER_KEY);

        if (!ObjectUtils.isEmpty(token)) {
            return token;
        }

        return null;
    }
}
