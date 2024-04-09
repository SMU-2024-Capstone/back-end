package capstone.courseweb.config;

import capstone.courseweb.oauth2.CustomOAuth2UserService;
import capstone.courseweb.oauth2.handler.OAuth2LoginFailureHandler;
import capstone.courseweb.oauth2.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity  // Spring Security 설정 활성화
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //== 기타 설정 ==//
                .formLogin(AbstractHttpConfigurer::disable) // FormLogin 사용 X
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 사용 X
                .csrf(AbstractHttpConfigurer::disable) // csrf 보안 사용 X
                // h2 접근 허용을 위한 코드
                .headers(
                        headersConfigurer ->
                                headersConfigurer
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::disable
                                        )
                )

                //== URL별 권한 관리 옵션 ==//
                // TODO: 페이지별 권한 설정 추후 변경
                // .anyRequest().authenticated() -> 로그인 한 모든 회원
                .authorizeHttpRequests(authorizeRequest ->
                                authorizeRequest
                                        .requestMatchers(AntPathRequestMatcher.antMatcher("/guest")
                                        ).hasRole("GUEST")
                                        .requestMatchers(AntPathRequestMatcher.antMatcher("/user")
                                        ).hasRole("USER")
                                        .anyRequest().permitAll()
                )

                //== 소셜 로그인 설정 ==//
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .successHandler(oAuth2LoginSuccessHandler)  // 동의하고 계속하기를 눌렀을 때 Handler 설정
                                .failureHandler(oAuth2LoginFailureHandler)  // 소셜 로그인 실패 시 핸들러 설정
                                .userInfoEndpoint(userInfoEndpoint ->   // oauth2 로그인 성공 후 가져올 때의 설정들
                                        // 소셜로그인 성공 시 후속 조치를 진행할 UserService 인터페이스 구현체 등록
                                        userInfoEndpoint.userService(customOAuth2UserService))  // 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시
                )
                /*
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")

                );

                 */
        ;
        return http.build();

    }
}
